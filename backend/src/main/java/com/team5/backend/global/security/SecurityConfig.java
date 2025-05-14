package com.team5.backend.global.security;

import com.team5.backend.global.filter.DeletedMemberAuthenticationFilter;
import com.team5.backend.global.filter.JwtAuthenticationFilter;
import com.team5.backend.global.handler.OAuth2AuthenticationSuccessHandler;
import com.team5.backend.global.util.JwtUtil;
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

import java.util.Arrays;

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
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
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
