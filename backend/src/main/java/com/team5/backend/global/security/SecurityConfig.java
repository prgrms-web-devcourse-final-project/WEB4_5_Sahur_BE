package com.team5.backend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.filter.DeletedMemberAuthenticationFilter;
import com.team5.backend.global.filter.JwtAuthenticationFilter;
import com.team5.backend.global.handler.OAuth2AuthenticationSuccessHandler;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final DeletedMemberAuthenticationFilter deletedMemberAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // OAuth2 관련 경로 허용
                        .requestMatchers("/oauth2/**", "/login/oauth2/code/**").permitAll()

                        // 인증이 필요한 GET 요청들
                        .requestMatchers(GET, getAuthenticatedGet()).authenticated()

                        // 인증이 필요한 POST 요청들
                        .requestMatchers(POST, getAuthenticatedPost()).authenticated()

                        // 인증이 필요한 PATCH 요청들
                        .requestMatchers(PATCH, getAuthenticatedPatch()).authenticated()

                        // 인증이 필요한 DELETE 요청들
                        .requestMatchers(DELETE, getAuthenticatedDelete()).authenticated()

                        // 나머지 모든 요청은 허용
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            AuthErrorCode errorCode = AuthErrorCode.UNAUTHORIZED;
                            sendErrorResponse(response, errorCode);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            AuthErrorCode errorCode = AuthErrorCode.FORBIDDEN;
                            sendErrorResponse(response, errorCode);
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOauth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        // OAuth2 로그인 페이지 경로 설정
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorization"))
                        // OAuth2 콜백 경로 설정
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/*"))
                )
                .addFilterBefore(deletedMemberAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeParameter("rememberMe")    // 클라이언트에서 사용할 파라미터 이름
                        .alwaysRemember(false)             // 체크박스 선택시에만 사용
                        .tokenValiditySeconds((int) (jwtUtil.getRememberMeExpiration() / 1000))  // 토큰 유효 시간
                        .userDetailsService(userDetailsService));

        return http.build();
    }

    /**
     * 인증이 필요한 GET 요청 경로들
     */
    private String[] getAuthenticatedGet() {

        return new String[]{
                "/api/v1/dibs",
                "/api/v1/groupBuy/member",
                "/api/v1/histories/products/*/writable-histories",
                "/api/v1/members/me",
                "/api/v1/notifications/member/list",
                "/api/v1/orders/me",
                "/api/v1/payments/me",
                "/api/v1/member/list",
                "/api/v1/reviews/member/list"
        };
    }

    // 인증이 필요한 POST 요청 경로들
    private String[] getAuthenticatedPost() {

        return new String[]{
                "/api/v1/dibs/products/*",
                "/api/v1/histories",
                "/api/v1/members/restore",
                "/api/v1/notifications",
                "/api/v1/orders",
                "/api/v1/reviews",
                "/api/v1/products/request"
        };
    }

    // 인증이 필요한 PATCH 요청 경로들
    private String[] getAuthenticatedPatch() {

        return new String[]{
                "/api/v1/members/modify"
        };
    }

    // 인증이 필요한 DELETE 요청 경로들
    private String[] getAuthenticatedDelete() {

        return new String[]{
                "/api/v1/dibs/products/*/dibs",
                "/api/v1/members/delete"
        };
    }

    private void sendErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {

        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json;charset=UTF-8");

        // 기존 응답 형식과 동일하게 맞춤
        Map<String, Object> errorResponse = Map.of(
                "success", false,
                "status", errorCode.getStatus(),
                "msg", errorCode.getCode(),
                "message", errorCode.getMessage()
        );

        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 배포 테스트 용
        configuration.addAllowedOriginPattern("*");
        //configuration.setAllowedOrigins(Arrays.asList("https://cdpn.io", AppConfig.getSiteFrontUrl()));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
