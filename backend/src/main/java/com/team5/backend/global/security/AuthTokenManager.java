package com.team5.backend.global.security;

import com.team5.backend.domain.member.member.dto.AuthResDto;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 토큰과 쿠키 관련 유틸리티 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenManager {

    private final JwtUtil jwtUtil;

    // 요청에서 액세스 토큰을 추출
    public String extractAccessToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    // 인증 객체에서 토큰 정보 가져오기
    public String extractToken(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        // Principal에서 이메일 정보 가져오기
        String email = switch (authentication.getPrincipal()) {
            case PrincipalDetails principalDetails -> principalDetails.getUsername();
            case UserDetails userDetails -> userDetails.getUsername();
            case String s -> s;
            default -> null;
        };

        if (email == null) {
            log.warn("인증 객체에서 이메일을 추출할 수 없습니다.");
            return null;
        }

        // 이메일을 통해 Redis에서 액세스 토큰 조회
        return jwtUtil.getStoredAccessToken(email);
    }

    // 요청에서 리프레시 토큰을 추출
    public String extractRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 응답에 쿠키를 추가
    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
    }

    // 토큰을 검증하고 유효한지 확인합니다.
    public String validateAccessToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        // 토큰이 블랙리스트에 있는지 확인
        if (jwtUtil.isTokenBlacklisted(token)) {
            throw new CustomException(AuthErrorCode.LOGOUT_TOKEN);
        }

        // 토큰에서 사용자 정보 추출
        String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // Redis에 저장된 토큰과 일치하는지 확인
        if (!jwtUtil.validateAccessTokenInRedis(email, token)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        return email;
    }

    // 리프레시 토큰을 검증하고 유효한지 확인합니다.
    public String validateRefreshToken(String refreshToken) {

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
        String email;
        try {
            email = jwtUtil.extractEmail(refreshToken);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에 저장된 리프레시 토큰과 비교
        if (!jwtUtil.validateRefreshTokenInRedis(email, refreshToken)) {
            throw new CustomException(AuthErrorCode.TOKEN_MISMATCH);
        }

        return email;
    }

    // 토큰을 갱신하고 새 토큰을 발급합니다.
    public AuthResDto refreshTokens(String oldAccessToken, String refreshToken, HttpServletResponse response) {

        // 리프레시 토큰 검증
        String email = validateRefreshToken(refreshToken);

        // 토큰에서 추가 정보 추출
        Long memberId = jwtUtil.extractMemberId(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        // 기존 액세스 토큰 무효화
        if (oldAccessToken != null) {
            try {
                jwtUtil.addToBlacklist(oldAccessToken);
            } catch (Exception e) {
                log.error("액세스 토큰 블랙리스트 추가 중 오류 발생: {}", e.getMessage());
            }
        }

        // 새로운 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(memberId, email, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(memberId, email, role);

        // 기존 리프레시 토큰 무효화
        jwtUtil.addRefreshTokenToBlacklist(refreshToken);

        // Redis에 리프레시 토큰 업데이트
        jwtUtil.updateRefreshTokenInRedis(email, newRefreshToken);

        // 쿠키에 새 토큰 저장
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);

        addCookie(response, "accessToken", newAccessToken, accessTokenMaxAge);
        addCookie(response, "refreshToken", newRefreshToken, refreshTokenMaxAge);

        return new AuthResDto(newAccessToken, newRefreshToken);
    }


     // 토큰을 무효화하고 쿠키를 삭제(로그아웃)
    public void invalidateTokens(String accessToken, HttpServletResponse response) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
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

        // 액세스 토큰 블랙리스트에 추가
        jwtUtil.addToBlacklist(accessToken);

        // Redis에서 리프레시 토큰 삭제
        jwtUtil.removeRefreshToken(email);

        // 쿠키 삭제
        addCookie(response, "accessToken", "", 0);
        addCookie(response, "refreshToken", "", 0);
    }

    // 요청에서 Remember Me 토큰을 추출
    public String extractRememberMeToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember-me".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Remember Me 토큰을 검증하고 유효한지 확인합니다.
    public String validateRememberMeToken(String rememberMeToken) {

        if (rememberMeToken == null || rememberMeToken.isEmpty()) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // 토큰 유효성 검증
        if (jwtUtil.isTokenExpired(rememberMeToken)) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        }

        // 토큰에서 사용자 정보 추출
        String email;
        try {
            email = jwtUtil.extractEmail(rememberMeToken);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // Redis에 저장된 Remember Me 토큰과 비교
        if (!jwtUtil.validateRememberMeTokenInRedis(email, rememberMeToken)) {
            throw new CustomException(AuthErrorCode.TOKEN_MISMATCH);
        }

        return email;
    }

    // Remember Me 토큰으로 자동 로그인 처리
    public AuthResDto autoLoginWithRememberMe(String rememberMeToken, HttpServletResponse response) {

        // Remember Me 토큰 검증
        String email = validateRememberMeToken(rememberMeToken);

        // 토큰에서 추가 정보 추출
        Long memberId = jwtUtil.extractMemberId(rememberMeToken);
        String role = jwtUtil.extractRole(rememberMeToken);

        // 새로운 액세스 및 리프레시 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(memberId, email, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(memberId, email, role);

        // 쿠키에 새 토큰 저장
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);

        addCookie(response, "accessToken", newAccessToken, accessTokenMaxAge);
        addCookie(response, "refreshToken", newRefreshToken, refreshTokenMaxAge);

        return new AuthResDto(newAccessToken, newRefreshToken);
    }

    // 로그아웃시 Remember Me 토큰도 함께 무효화
    public void invalidateTokensWithRememberMe(String accessToken, HttpServletResponse response) {

        // 기존 invalidateTokens 메서드와 동일한 로직 수행
        if (accessToken == null || accessToken.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
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

        // Remember Me 토큰 무효화 (추가된 부분)
        jwtUtil.invalidateRememberMeToken(email);

        // 액세스 토큰 블랙리스트에 추가
        jwtUtil.addToBlacklist(accessToken);

        // Redis에서 리프레시 토큰 삭제
        jwtUtil.removeRefreshToken(email);

        // 쿠키 삭제
        addCookie(response, "accessToken", "", 0);
        addCookie(response, "refreshToken", "", 0);
        addCookie(response, "remember-me", "", 0);  // Remember Me 쿠키도 삭제
    }
}
