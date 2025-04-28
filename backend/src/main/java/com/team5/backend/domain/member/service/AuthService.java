package com.team5.backend.domain.member.service;

import com.team5.backend.domain.member.dto.LoginReqDto;
import com.team5.backend.domain.member.dto.LoginResDto;
import com.team5.backend.domain.member.dto.TokenInfoDto;
import com.team5.backend.domain.member.entity.Member;
import com.team5.backend.domain.member.repository.MemberRepository;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {

        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(loginReqDto.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if ((!loginReqDto.getPassword().equals(member.getPassword()))) {
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

    // 토큰 갱신 메서드
    @Transactional
    public LoginResDto refreshToken(String refreshToken, HttpServletResponse response) {

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

        return new LoginResDto(newAccessToken, refreshToken, memberId);
    }

    // 로그아웃 메서드
    @Transactional
    public void logout(String accessToken, HttpServletResponse response) {

        // 토큰 블랙리스트에 추가하여 무효화
        jwtUtil.addToBlacklist(accessToken);

        // 쿠키 제거
        addCookie(response, "accessToken", "", 0);
        addCookie(response, "refreshToken", "", 0);
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
        TokenInfoDto tokenInfo = extractTokenInfo(extractedToken);

        return memberRepository.findByEmail(tokenInfo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // 토큰에서 사용자 정보를 추출하는 메서드
    public TokenInfoDto extractTokenInfo(String token) {

        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("만료된 토큰입니다.");
        }

        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        return new TokenInfoDto(email, role);
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
}
