package com.team5.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.security.CustomUserDetailsService;
import com.team5.backend.global.util.JwtUtil;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class DeletedMemberAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthTokenManager authTokenManager;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 탈퇴 회원 복구 엔드포인트에 대해서만 동작
        String requestPath = request.getServletPath();
        if (!requestPath.equals("/api/v1/members/restore")) {

            filterChain.doFilter(request, response);
            return ;
        }

        log.info("DeletedMemberAuthenticationFilter 처리 중: {}", requestPath);

        // 임시 토큰 추출 (탈퇴 회원용 토큰)
        String tempToken = authTokenManager.extractAccessToken(request);

        if (tempToken == null) {

            sendErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
            return ;
        }

        try {
            // 임시 토큰 검증
            if (jwtUtil.isTokenExpired(tempToken)) {

                sendErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
                return ;
            }

            // 삭제된 회원용 토큰인지 확인
            if (!jwtUtil.isDeletedMemberToken(tempToken)) {

                sendErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
                return ;
            }

            // 이메일 추출
            String email = jwtUtil.extractEmail(tempToken);

            try {
                // UserDetailsService를 통해 삭제된 회원 정보 로드
                UserDetails userDetails = customUserDetailsService.loadDeletedUserByUsername(email);

                // 인증 객체 생성 및 Security Context에 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContextHolder에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 필터 체인 계속 진행
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                sendErrorResponse(response, MemberErrorCode.MEMBER_NOT_FOUND);
            }
        } catch (Exception e) {
            sendErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
        }
    }

    // 오류 응답 전송
    private void sendErrorResponse(HttpServletResponse response, com.team5.backend.global.exception.ErrorCode errorCode) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        RsData<Empty> errorResponse = RsDataUtil.fail(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
