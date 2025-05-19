package com.team5.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.member.member.dto.AuthResDto;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.ErrorCode;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.CommonErrorCode;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.security.CookieRequestWrapper;
import com.team5.backend.global.security.CustomUserDetailsService;
import com.team5.backend.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthTokenManager authTokenManager;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 회원 복구일 경우 JwtAuthenticationFilter에서는 처리하지 않고 필터 체인을 계속 진행
        String requestPath = request.getServletPath();
        if (requestPath.startsWith("/h2-console") || requestPath.equals("/api/v1/members/restore")) {

            filterChain.doFilter(request, response);
            return ;
        }

        String accessToken = authTokenManager.extractAccessToken(request);
        String refreshToken = authTokenManager.extractRefreshToken(request);
        String rememberMeToken = authTokenManager.extractRememberMeToken(request); // Remember Me 토큰 추출

        // 토큰이 존재하지 않으면 필터 체인을 계속 진행
        if (accessToken == null) {

            // 리프레시 토큰도 없지만 Remember Me 토큰이 있는 경우 - 자동 로그인 시도
            if (refreshToken == null && rememberMeToken != null) {
                try {
                    log.info("Remember Me 토큰으로 자동 로그인 시도");

                    // Remember Me 토큰 검증 및 자동 로그인 처리
                    AuthResDto authResDto = authTokenManager.autoLoginWithRememberMe(rememberMeToken, response);

                    // 새 액세스 토큰으로 사용자 정보 로드
                    String email = jwtUtil.extractEmail(authResDto.getAccessToken());

                    // UserDetailsService를 통해 사용자 정보 로드
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    // 인증 객체 생성 및 Security Context에 설정
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContextHolder에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 쿠키 값을 담은 Map 생성
                    Map<String, String> newCookies = new HashMap<>();
                    newCookies.put("accessToken", authResDto.getAccessToken());
                    newCookies.put("refreshToken", authResDto.getRefreshToken());

                    // 새 쿠키 값을 포함한 요청 래퍼 생성
                    HttpServletRequest wrappedRequest = new CookieRequestWrapper(request, newCookies);

                    // 응답 헤더에 토큰 갱신 표시 (선택 사항)
                    response.setHeader("X-Token-Refreshed", "true");

                    // 필터 체인 계속 진행 (원래 API 요청 처리)
                    filterChain.doFilter(wrappedRequest, response);
                    return ;
                } catch (Exception e) {

                    log.info("Remember Me 토큰으로 자동 로그인 실패: {}", e.getMessage());
                    // Remember Me 쿠키 삭제
                    authTokenManager.addCookie(response, "remember-me", "", 0);

                    // 인증되지 않은 상태로 진행
                    filterChain.doFilter(request, response);
                    return ;
                }
            }

            // 토큰이 없으면 필터 체인을 계속 진행
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

            // 삭제된 회원을 위한 로그아웃
            if (jwtUtil.isDeletedMemberToken(accessToken)) {

                log.info("삭제된 회원의 토큰 감지: {}", email);

                if (requestPath.equals("/api/v1/auth/logout")) {
                    // 로그아웃 요청은 진행시킴
                    try {
                        // 삭제된 회원 정보 로드
                        UserDetails userDetails = customUserDetailsService.loadDeletedUserByUsername(email);

                        // 인증 객체 생성 및 Security Context에 설정
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 로그아웃 요청 계속 진행
                        filterChain.doFilter(request, response);
                        return;
                    } catch (Exception e) {
                        sendTokenErrorResponse(response, CommonErrorCode.VALIDATION_ERROR);
                        return;
                    }
                } else {
                    sendTokenErrorResponse(response, CommonErrorCode.VALIDATION_ERROR);
                    return;
                }
            }

            // 액세스 토큰이 유효하고 Redis에 저장된 토큰과 일치하면 요청 진행
            if (!jwtUtil.isTokenExpired(accessToken) && jwtUtil.validateAccessTokenInRedis(email, accessToken)) {

                try {
                    // UserDetailsService를 통해 사용자 정보 로드
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    // 인증 객체 생성 및 Security Context에 설정
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContextHolder에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    log.info("인증 컨텍스트 설정 중 오류 발생", e);
                    // 오류가 발생해도 요청은 계속 진행
                }

                filterChain.doFilter(request, response);
                return ;
            }

            // 이 시점까지 왔다면 액세스 토큰이 만료되었거나 Redis에 없음 → 리프레시 토큰으로 갱신 시도
            log.info("자동 갱신 시도 - 액세스 토큰 만료 또는 불일치. 리프레시 토큰 존재: {}", refreshToken != null);

            if (refreshToken != null) {
                try {
                    refreshAndContinue(request, response, filterChain, accessToken, refreshToken);
                } catch (Exception e) {
                    log.info("리프레시 토큰 처리 중 오류 발생", e);
                    sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);
                }
            } else {
                log.info("리프레시 토큰을 찾을 수 없음");
                sendTokenErrorResponse(response, AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

        } catch (ExpiredJwtException e) {
            // 액세스 토큰이 만료된 경우
            log.info("액세스 토큰 만료 감지 - 갱신 시도");

            if (refreshToken != null) {
                try {
                    refreshAndContinue(request, response, filterChain, accessToken, refreshToken);
                } catch (Exception ex) {
                    log.info("리프레시 토큰 처리 중 오류 발생", ex);
                    sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);
                }
            } else {
                log.info("리프레시 토큰을 찾을 수 없음");
                sendTokenErrorResponse(response, AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("토큰 처리 중 예외 발생", e);
            sendTokenErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
        }
    }

    // 토큰 오류 응답 전송
    private void sendTokenErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        RsData<Empty> errorResponse = RsDataUtil.fail(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void refreshAndContinue(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
                                    String accessToken, String refreshToken) throws IOException {
        try {

            // 토큰 갱신
            AuthResDto authResDto = authTokenManager.refreshTokens(accessToken, refreshToken, response);
            String newAccessToken = authResDto.getAccessToken();
            String newRefreshToken = authResDto.getRefreshToken();

            // 새 액세스 토큰으로 사용자 정보 로드
            String email = jwtUtil.extractEmail(newAccessToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // 인증 객체 생성 및 SecurityContext에 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 쿠키 값을 담은 Map 생성 (쿠키 이름은 실제 코드에 맞게 수정 필요)
            Map<String, String> newCookies = new HashMap<>();
            newCookies.put("access-token", newAccessToken);
            newCookies.put("refresh-token", newRefreshToken);

            // 새 쿠키 값을 포함한 요청 래퍼 생성
            HttpServletRequest wrappedRequest = new CookieRequestWrapper(request, newCookies);

            // 응답 헤더에 토큰 갱신 표시 (선택 사항)
            response.setHeader("X-Token-Refreshed", "true");

            // 필터 체인 계속 진행 (원래 API 요청 처리)
            filterChain.doFilter(wrappedRequest, response);
        } catch (Exception e) {

            log.info("토큰 갱신 및 요청 처리 중 오류", e);
            sendTokenErrorResponse(response, AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}