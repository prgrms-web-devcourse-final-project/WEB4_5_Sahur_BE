package com.team5.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.global.exception.code.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *  JwtAuthenticationFilter의 tokenAuthentication 메소드에서
 *  토큰 인증 오류가 발생하였을 때 실행되는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        log.error("Unauthorized error: {}", authException.getMessage());

        // 이미 토큰 갱신을 시도했다면 별도 응답 처리 없이 통과
        Boolean refreshedToken = (Boolean) request.getAttribute("refreshedToken");
        if (refreshedToken != null && refreshedToken) {
            return;
        }

        // 토큰 만료 여부 확인
        boolean isTokenExpired = request.getAttribute("expired") != null;

        AuthErrorCode errorCode = isTokenExpired
                ? AuthErrorCode.EXPIRED_TOKEN
                : AuthErrorCode.UNAUTHORIZED;

        Map<String, Object> errorResponse = new HashMap<>();

        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("code", errorCode.getCode());
        errorResponse.put("message", errorCode.getMessage());
        errorResponse.put("path", request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
