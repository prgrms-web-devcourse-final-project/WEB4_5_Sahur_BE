package com.team5.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.member.member.dto.TokenRefreshResDto;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.ErrorCode;
import com.team5.backend.global.exception.RsDataUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = extractToken(request);

        // 토큰이 존재하지 않으면 필터 체인을 계속 진행
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return ;
        }

        try {
            // 토큰이 블랙리스트에 있는지 확인
            if (jwtUtil.isTokenBlacklisted(accessToken)) {
                sendTokenErrorResponse(response, AuthErrorCode.LOGOUT_TOKEN);
                return ;
            }

            // 이메일 추출 및 토큰 유효성 검증
            String email = jwtUtil.extractEmail(accessToken);

            // 액세스 토큰이 유효하고 Redis에 저장된 토큰과 일치하면 요청 진행
            if (!jwtUtil.isTokenExpired(accessToken) && jwtUtil.validateAccessTokenInRedis(email, accessToken)) {
                filterChain.doFilter(request, response);
                return ;
            }

            // 이 시점까지 왔다면 액세스 토큰이 만료되었거나 Redis에 없음 → 리프레시 토큰으로 갱신 시도
            String refreshToken = extractRefreshToken(request);
            log.info("자동 갱신 시도 - 액세스 토큰 만료 또는 불일치. 리프레시 토큰 존재: {}", refreshToken != null);

            if (refreshToken != null) {
                try {
                    // 리프레시 토큰이 블랙리스트에 있는지 확인
                    if (jwtUtil.isRefreshTokenBlacklisted(refreshToken)) {

                        log.info("리프레시 토큰이 블랙리스트에 있음");
                        sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);

                        return ;
                    }

                    // 리프레시 토큰 만료 여부 확인
                    if (jwtUtil.isTokenExpired(refreshToken)) {

                        log.info("리프레시 토큰이 만료됨");
                        sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);

                        return ;
                    }

                    // 리프레시 토큰에서 사용자 정보 추출
                    String tokenEmail = jwtUtil.extractEmail(refreshToken);
                    Long memberId = jwtUtil.extractMemberId(refreshToken);
                    String role = jwtUtil.extractRole(refreshToken);
                    log.info("리프레시 토큰에서 추출한 정보 - 이메일: {}, 역할: {}", tokenEmail, role);

                    // Redis에 저장된 리프레시 토큰과 일치하는지 확인
                    if (!jwtUtil.validateRefreshTokenInRedis(tokenEmail, refreshToken)) {
                        log.info("리프레시 토큰이 Redis에 저장된 토큰과 일치하지 않음");
                        sendTokenErrorResponse(response, AuthErrorCode.TOKEN_MISMATCH);
                        return ;
                    }

                    // 기존 액세스 토큰을 블랙리스트에 등록
                    log.info("이전 액세스 토큰 블랙리스트에 추가 시도");
                    jwtUtil.addToBlacklist(accessToken);

                    // 새로운 액세스 토큰 발급
                    String newAccessToken = jwtUtil.generateAccessToken(memberId, tokenEmail, role);

                    // 새로운 리프레시 토큰 발급 (롤링 리프레시)
                    String newRefreshToken = jwtUtil.generateRefreshToken(memberId, tokenEmail, role);
                    log.info("새 토큰 발급 완료 - 액세스 토큰: {}, 리프레시 토큰: {}",
                            newAccessToken.substring(0, 10) + "...",
                            newRefreshToken.substring(0, 10) + "...");

                    // 기존 리프레시 토큰을 블랙리스트에 등록
                    jwtUtil.addRefreshTokenToBlacklist(refreshToken);

                    // Redis에 리프레시 토큰 업데이트
                    jwtUtil.updateRefreshTokenInRedis(tokenEmail, newRefreshToken);

                    // 쿠키 설정
                    addCookie(response, "accessToken", newAccessToken, (int)(jwtUtil.getAccessTokenExpiration() / 1000));
                    addCookie(response, "refreshToken", newRefreshToken, (int)(jwtUtil.getRefreshTokenExpiration() / 1000));

                    // 클라이언트에게 토큰이 갱신되었음을 알리는 응답
                    sendTokenRefreshResponse(response, newAccessToken, newRefreshToken);
                } catch (Exception e) {

                    log.error("리프레시 토큰 처리 중 오류 발생", e);
                    sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);
                }
            } else {
                log.info("리프레시 토큰을 찾을 수 없음");
                sendTokenErrorResponse(response, AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

        } catch (ExpiredJwtException e) {
            // 액세스 토큰이 만료된 경우
            log.info("액세스 토큰 만료 감지 - 갱신 시도");
            String refreshToken = extractRefreshToken(request);

            if (refreshToken != null) {
                try {
                    // 리프레시 토큰이 블랙리스트에 있는지 확인
                    if (jwtUtil.isRefreshTokenBlacklisted(refreshToken)) {
                        
                        log.info("리프레시 토큰이 블랙리스트에 있음");
                        sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);
                        
                        return ;
                    }

                    // 리프레시 토큰에서 사용자 정보 추출
                    String tokenEmail = jwtUtil.extractEmail(refreshToken);
                    Long memberId = jwtUtil.extractMemberId(refreshToken);
                    String role = jwtUtil.extractRole(refreshToken);
                    log.info("리프레시 토큰에서 추출한 정보 - 이메일: {}, 역할: {}", tokenEmail, role);

                    // Redis에 저장된 리프레시 토큰과 일치하는지 확인
                    if (!jwtUtil.validateRefreshTokenInRedis(tokenEmail, refreshToken)) {
                        
                        log.info("리프레시 토큰이 Redis에 저장된 토큰과 일치하지 않음");
                        sendTokenErrorResponse(response, AuthErrorCode.TOKEN_MISMATCH);
                        return ;
                    }

                    // 만료된 액세스 토큰을 블랙리스트에 등록
                    try {
                        if (e.getClaims() != null && e.getClaims().getSubject() != null) {
                            log.info("만료된 액세스 토큰 블랙리스트에 추가");
                            jwtUtil.addToBlacklist(accessToken);
                        }
                    } catch (Exception ex) {
                        log.error("만료된 토큰 블랙리스트 추가 중 오류 발생: {}", ex.getMessage());
                    }

                    // 새로운 액세스 토큰 발급
                    String newAccessToken = jwtUtil.generateAccessToken(memberId, tokenEmail, role);

                    // 새로운 리프레시 토큰 발급 (롤링 리프레시)
                    String newRefreshToken = jwtUtil.generateRefreshToken(memberId, tokenEmail, role);
                    log.info("새 토큰 발급 완료 - 액세스 토큰: {}, 리프레시 토큰: {}",
                            newAccessToken.substring(0, 10) + "...",
                            newRefreshToken.substring(0, 10) + "...");

                    // 기존 리프레시 토큰을 블랙리스트에 등록
                    jwtUtil.addRefreshTokenToBlacklist(refreshToken);

                    // Redis에 리프레시 토큰 업데이트
                    jwtUtil.updateRefreshTokenInRedis(tokenEmail, newRefreshToken);

                    // 쿠키 설정
                    addCookie(response, "accessToken", newAccessToken, (int)(jwtUtil.getAccessTokenExpiration() / 1000));
                    addCookie(response, "refreshToken", newRefreshToken, (int)(jwtUtil.getRefreshTokenExpiration() / 1000));

                    // 클라이언트에게 토큰이 갱신되었음을 알리는 응답
                    sendTokenRefreshResponse(response, newAccessToken, newRefreshToken);
                } catch (Exception ex) {

                    log.error("리프레시 토큰 처리 중 오류 발생", ex);
                    sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);
                }
            } else {

                log.info("리프레시 토큰을 찾을 수 없음");
                sendTokenErrorResponse(response, AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);

            }
        } catch (Exception e) {

            log.error("토큰 처리 중 예외 발생", e);
            sendTokenErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
        }
    }

    // 액세스 토큰 추출 (Authorization 헤더에서)
    private String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    // 리프레시 토큰 추출 (쿠키에서)
    private String extractRefreshToken(HttpServletRequest request) {

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

    // 토큰 오류 응답 전송
    private void sendTokenErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        RsData<Empty> errorResponse = RsDataUtil.fail(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    // 토큰 갱신 응답 전송
    private void sendTokenRefreshResponse(HttpServletResponse response, String newAccessToken, String newRefreshToken) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 새 토큰 정보를 담은 응답 객체
        TokenRefreshResDto tokenRefreshResDto = new TokenRefreshResDto(newAccessToken, newRefreshToken, true);

        RsData<TokenRefreshResDto> refreshResponse = RsDataUtil.success("토큰이 재발급되었습니다.", tokenRefreshResDto);
        response.getWriter().write(objectMapper.writeValueAsString(refreshResponse));
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");

        response.addCookie(cookie);
    }
}