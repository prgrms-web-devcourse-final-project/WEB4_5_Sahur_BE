package com.team5.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.member.member.dto.AuthResDto;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;
        String refreshToken = null;

        // Authorization 헤더에서 액세스 토큰 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        }

        // 쿠키에서 리프레시 토큰 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            refreshToken = Arrays.stream(cookies)
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }

        // 액세스 토큰이 없는 경우는 인증 실패로 처리
        if (accessToken == null) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 액세스 토큰 검증 및 인증 처리
            tokenAuthentication(accessToken, response);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // 액세스 토큰이 만료된 경우, 리프레시 토큰으로 갱신 시도
            handleExpiredAccessToken(accessToken, refreshToken, request, response, filterChain);
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("토큰 갱신 오류: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private void tokenAuthentication(String accessToken, HttpServletResponse response) {

        // 토큰이 블랙리스트에 있는지 확인
        if (jwtUtil.isTokenBlacklisted(accessToken)) {
            throw new CustomException(AuthErrorCode.LOGOUT_TOKEN);
        }

        // 토큰에서 사용자 정보 추출
        String email = jwtUtil.extractEmail(accessToken);
        String role = jwtUtil.extractRole(accessToken);

        // Redis에 저장된 토큰과 일치하는지 확인
        if (!jwtUtil.validateAccessTokenInRedis(email, accessToken)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // 인증 정보 설정
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleExpiredAccessToken(String expiredAccessToken, String refreshToken, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 만료된 토큰에서도 사용자 정보 추출 가능한 메서드 사용
            String email = jwtUtil.extractEmailIgnoringExpiration(expiredAccessToken);

            // 리프레시 토큰 확인
            if (refreshToken != null && !jwtUtil.isTokenExpired(refreshToken)
                    && !jwtUtil.isTokenBlacklisted(refreshToken)
                    && jwtUtil.validateRefreshTokenInRedis(email, refreshToken)) {

                // 리프레시 토큰으로 새 액세스 토큰 발급
                Long memberId = jwtUtil.extractMemberId(refreshToken);
                String role = jwtUtil.extractRole(refreshToken);

                // 새 액세스 토큰 생성
                String newAccessToken = jwtUtil.generateAccessToken(memberId, email, role);

                // 액세스 토큰 갱신 시 항상 리프레시 토큰도 갱신
                String newRefreshToken = jwtUtil.generateRefreshToken(memberId, email, role);

                // 기존 리프레시 토큰 블랙리스트에 추가 (이전 토큰 무효화)
                jwtUtil.addToBlacklist(refreshToken);

                // 새 리프레시 토큰을 Redis에 저장
                jwtUtil.updateRefreshTokenInRedis(email, newRefreshToken);

                // 쿠키에 새 리프레시 토큰 저장
                addCookie(response, "refreshToken", newRefreshToken, (int) (jwtUtil.getRefreshTokenExpiration() / 1000));

                // 응답 헤더에 새 액세스 토큰 추가
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                // 쿠키에 새 액세스 토큰 저장
                addCookie(response, "accessToken", newAccessToken, (int) (jwtUtil.getAccessTokenExpiration() / 1000));

                // 현재 요청에 새 토큰 정보 설정
                request.setAttribute("refreshedToken", true);

                // 새 토큰으로 인증 처리
                tokenAuthentication(newAccessToken, response);

                // 토큰 갱신 정보를 응답에 추가
                AuthResDto authResDto = new AuthResDto(newAccessToken, newRefreshToken);
                response.setHeader("Content-Type", "application/json");
                response.getWriter().write(objectMapper.writeValueAsString(authResDto));

                filterChain.doFilter(request, response);
            } else {
                // 리프레시 토큰이 유효하지 않은 경우 인증 실패
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {

            log.error("갱신 과정 중 오류 발생: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    // 쿠키 추가 유틸리티 메서드
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
