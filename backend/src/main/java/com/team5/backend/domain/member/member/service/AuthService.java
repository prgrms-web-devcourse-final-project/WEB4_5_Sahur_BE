package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_LOGIN_INFO));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_LOGIN_INFO);
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
    public void logout(String token, HttpServletResponse response) {

        String accessToken = token;

        if (accessToken == null || accessToken.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        // Bearer 접두사가 있는 경우 제거
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        // 토큰에서 이메일 추출
        String email = jwtUtil.extractEmail(accessToken);

        // 토큰 유효성 검증
        if (!jwtUtil.validateToken(accessToken, email)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // 리프레시 토큰 조회 후 블랙리스트에 추가
        String refreshToken = jwtUtil.getStoredRefreshToken(email);
        if (refreshToken != null) {
            jwtUtil.addRefreshTokenToBlacklist(refreshToken);
        }

        // 액세스 토큰 블랙리스트에 추가하여 무효화
        jwtUtil.addToBlacklist(accessToken);

        // Redis에서 리프레시 토큰 삭제
        jwtUtil.removeRefreshToken(email);

        addCookie(response, "accessToken", "", 0);
        addCookie(response, "refreshToken", "", 0);
    }

    // 토큰 갱신 메서드
    @Transactional
    public AuthResDto refreshToken(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 리프레시 토큰 유효성 검증
        if (jwtUtil.isTokenExpired(refreshToken)) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰이 블랙리스트에 있는지 확인
        if (jwtUtil.isRefreshTokenBlacklisted(refreshToken)) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰에서 사용자 정보 추출
        String email = null;
        Long memberId = null;
        String role = null;

        try {
            email = jwtUtil.extractEmail(refreshToken);
            memberId = jwtUtil.extractMemberId(refreshToken);
            role = jwtUtil.extractRole(refreshToken);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에 저장된 리프레시 토큰과 비교
        if (!jwtUtil.validateRefreshTokenInRedis(email, refreshToken)) {
            throw new CustomException(AuthErrorCode.TOKEN_MISMATCH);
        }

        // 기존 액세스 토큰 조회 및 블랙리스트에 추가
        String oldAccessToken = jwtUtil.getStoredAccessToken(email);
        if (oldAccessToken != null) {
            jwtUtil.addToBlacklist(oldAccessToken);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(memberId, email, role);

        // 쿠키에 새 액세스 토큰 저장
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        addCookie(response, "accessToken", newAccessToken, accessTokenMaxAge);

        // 리프레시 토큰 롤링 적용
        String newRefreshToken = jwtUtil.generateRefreshToken(memberId, email, role);

        // 쿠키에 새 리프레시 토큰 저장
        int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);
        addCookie(response, "refreshToken", newRefreshToken, refreshTokenMaxAge);

        // 기존 리프레시 토큰을 블랙리스트에 추가하고 Redis에 새 토큰 저장
        jwtUtil.updateRefreshTokenInRedis(email, newRefreshToken);

        return new AuthResDto(newAccessToken, newRefreshToken);
    }

    // 로그인된 사용자의 정보를 반환하는 메서드
    public GetMemberResDto getLoggedInMember(String token) {

        if (token == null || token.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        // 토큰에서 "Bearer "를 제거
        String extractedToken = token.replace("Bearer ", "");

        // 토큰이 블랙리스트에 있는지 확인
        if (jwtUtil.isTokenBlacklisted(extractedToken)) {
            throw new CustomException(AuthErrorCode.LOGOUT_TOKEN);
        }

        // 토큰에서 사용자 정보 추출
        String email;
        try {
            email = jwtUtil.extractEmail(extractedToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // Redis에 저장된 토큰과 일치하는지 확인
        if (!jwtUtil.validateAccessTokenInRedis(email, extractedToken)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        return memberRepository.findByEmail(email)
                .map(GetMemberResDto::fromEntity)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // 토큰에서 사용자 정보를 추출하는 메서드
    public TokenInfoResDto extractTokenInfo(String token) {

        if (jwtUtil.isTokenExpired(token)) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
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
        cookie.setSecure(true); // HTTPS 환경에서만 사용 가능
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
    }
}