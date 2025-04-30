package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {

        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 액세스 토큰 생성 - Redis에 저장되고 클라이언트에 반환됨
        String accessToken = jwtUtil.generateAccessToken(
                member.getMemberId(), member.getEmail(), member.getRole().name());

        // 리프레시 토큰 생성 - Redis에 저장되고 클라이언트에 반환됨
        String refreshToken = jwtUtil.generateRefreshToken(
                member.getMemberId(), member.getEmail(), member.getRole().name());

        // 쿠키에 토큰 저장
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);

        addCookie(response, "accessToken", accessToken, accessTokenMaxAge);
        addCookie(response, "refreshToken", refreshToken, refreshTokenMaxAge);

        // 로그인 응답 DTO 생성 및 반환
        return new LoginResDto(accessToken, refreshToken, member.getMemberId());
    }

    // 로그아웃 메서드
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // 액세스 토큰을 쿠키에서 추출
        String accessToken = extractCookieValue(request, "accessToken");

        if (accessToken == null) {
            throw new RuntimeException("액세스 토큰이 없습니다.");
        }

        // 토큰에서 사용자 이메일 추출
        String email = jwtUtil.extractEmail(accessToken);

        // 토큰 블랙리스트에 추가하여 무효화
        jwtUtil.addToBlacklist(accessToken);

        // Redis에서 리프레시 토큰 삭제
        jwtUtil.removeRefreshToken(email);

        // 쿠키 제거
        addCookie(response, "accessToken", "", 0);
        addCookie(response, "refreshToken", "", 0);
    }

    // 토큰 갱신 메서드
    @Transactional
    public AuthResDto refreshToken(String refreshToken, HttpServletResponse response) {

        // 리프레시 토큰 유효성 검증
        if (jwtUtil.isTokenExpired(refreshToken) || jwtUtil.isTokenBlacklisted(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 토큰에서 사용자 정보 추출
        String email = jwtUtil.extractEmail(refreshToken);
        Long memberId = jwtUtil.extractMemberId(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        // Redis에 저장된 리프레시 토큰과 비교
        if (!jwtUtil.validateRefreshTokenInRedis(email, refreshToken)) {
            throw new RuntimeException("토큰이 일치하지 않습니다.");
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(memberId, email, role);

        // 쿠키에 새 액세스 토큰 저장
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        addCookie(response, "accessToken", newAccessToken, accessTokenMaxAge);

        // 리프레시 토큰 갱신 필요 여부 확인
        String newRefreshToken = refreshToken;

        if (isRefreshTokenNeedsRenewal(refreshToken)) {
            // 새로운 리프레시 토큰 생성
            newRefreshToken = jwtUtil.generateRefreshToken(memberId, email, role);

            // 쿠키에 새 리프레시 토큰 저장
            int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);
            addCookie(response, "refreshToken", newRefreshToken, refreshTokenMaxAge);

            // Redis에 저장된 리프레시 토큰 업데이트
            jwtUtil.updateRefreshTokenInRedis(email, newRefreshToken);
        }

        return new AuthResDto(newAccessToken, newRefreshToken);
    }


    // 리프레시 토큰 갱신 필요 여부 확인
    private boolean isRefreshTokenNeedsRenewal(String refreshToken) {
        try {
            Date expiration = jwtUtil.extractExpiration(refreshToken);

            // 만료까지 남은 시간 계산 (밀리초)
            long timeToExpire = expiration.getTime() - System.currentTimeMillis();

            // 만료 기간의 30% 이하로 남았으면 갱신
            return timeToExpire < (jwtUtil.getRefreshTokenExpiration() * 0.3);
        } catch (Exception e) {
            return true;
        }
    }

    // 로그인된 사용자의 정보를 반환하는 메서드
    public Member getLoggedInMember(String token) {

        // 토큰에서 "Bearer "를 제거
        String extractedToken = token.replace("Bearer ", "");

        // 토큰이 블랙리스트에 있는지 확인
        if (jwtUtil.isTokenBlacklisted(extractedToken)) {
            throw new RuntimeException("로그아웃된 토큰입니다.");
        }

        // Redis에 저장된 토큰과 일치하는지 확인
        String username = jwtUtil.extractEmail(extractedToken);

        if (!jwtUtil.validateAccessTokenInRedis(username, extractedToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 토큰에서 사용자 정보 추출
        TokenInfoResDto tokenInfo = extractTokenInfo(extractedToken);

        return memberRepository.findByEmail(tokenInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // 토큰에서 사용자 정보를 추출하는 메서드
    public TokenInfoResDto extractTokenInfo(String token) {

        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("만료된 토큰입니다.");
        }

        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        return new TokenInfoResDto(email, role);
    }

    // 쿠키 생성 메서드 (만료 시간 설정)
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setMaxAge(maxAge); // 만료 시간 설정
        cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
//        cookie.setSecure(true); // HTTPS 환경에서만 사용 가능

        response.addCookie(cookie);
    }

    // 쿠키에서 값을 추출하는 유틸리티 메서드
    private String extractCookieValue(HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
