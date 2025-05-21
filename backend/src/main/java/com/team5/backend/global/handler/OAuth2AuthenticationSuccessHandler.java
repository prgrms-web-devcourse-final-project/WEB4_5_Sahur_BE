package com.team5.backend.global.handler;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.global.app.AppConfig;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.security.MemberTokenInfo;
import com.team5.backend.global.security.PrincipalDetails;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final AuthTokenManager authTokenManager;
    private final AppConfig appConfig;

    // 배포 환경 확인 메서드
    private boolean isProductionEnvironment() {
        String backUrl = appConfig.getSiteBackUrl();
        String frontUrl = appConfig.getSiteFrontUrl();

        // 배포 환경 여부 확인 로직 (URL로 판단하거나 프로필로 판단)
        return (backUrl != null && frontUrl != null &&
                !backUrl.contains("localhost") && !frontUrl.contains("localhost"));
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            Member member = principalDetails.getMember();

            log.info("OAuth2 인증 성공! 사용자: {}", member.getEmail());

            MemberTokenInfo memberTokenInfo = MemberTokenInfo.from(member);

            // JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(memberTokenInfo);

            // JWT 리프레시 토큰 생성
            String refreshToken = jwtUtil.generateRefreshToken(memberTokenInfo);

            // 토큰 만료 시간 계산 - 정확히 초 단위로 변환
            int accessTokenMaxAge = (int)(jwtUtil.getAccessTokenExpiration() / 1000);
            int refreshTokenMaxAge = (int)(jwtUtil.getRefreshTokenExpiration() / 1000);

            // 배포 환경과 개발 환경 분리 처리
            if (isProductionEnvironment()) {
                log.info("배포 환경에서 쿠키 설정 - 프론트엔드 URL: {}", appConfig.getSiteFrontUrl());

                // 프론트엔드 도메인 추출 (https://domain.com -> domain.com)
                String domain = null;
                try {
                    URL url = new URL(appConfig.getSiteFrontUrl());
                    domain = url.getHost();

                    // 최상위 도메인만 추출 (예: www.example.com -> example.com)
                    if (domain.startsWith("www.")) {
                        domain = domain.substring(4);
                    }

                    log.info("추출된 쿠키 도메인: {}", domain);
                } catch (Exception e) {
                    log.info("도메인 추출 중 오류: {}", e.getMessage(), e);
                    // 도메인 추출에 실패하면 도메인 설정 없이 진행
                    domain = null;
                }

                // 배포 환경에서의 쿠키 설정
                try {

                    setCookieHeader(response, "accessToken", accessToken, accessTokenMaxAge, domain);
                    setCookieHeader(response, "refreshToken", refreshToken, refreshTokenMaxAge, domain);

                    log.info("쿠키 설정 완료");
                } catch (Exception e) {
                    log.info("쿠키 설정 중 오류: {}", e.getMessage(), e);
                }
            } else {

                // 개발 환경에서는 기존 방식 사용
                log.info("개발 환경에서 쿠키 설정");
                authTokenManager.addCookie(response, "accessToken", accessToken, accessTokenMaxAge);
                authTokenManager.addCookie(response, "refreshToken", refreshToken, refreshTokenMaxAge);
            }

            // 리디렉션 URI 가져오기
            String targetUrl = determineTargetUrl(request, response, authentication);

            // 리디렉션 URI에 인증 성공 상태 포함
            targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("auth_status", "success")
                    .queryParam("user_email", member.getEmail())
                    .build().toUriString();

            log.info("최종 리다이렉트 URL: {}", targetUrl);

            // 리디렉션 수행
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {

            log.info("OAuth2 인증 성공 처리 중 예외 발생: {}", e.getMessage(), e);
            // 오류 발생 시 기본 로그인 페이지로 리디렉션
            getRedirectStrategy().sendRedirect(request, response,
                    appConfig.getSiteFrontUrl() + "/login?info=auth_info");
        }
    }

    // 배포 환경에서 쿠키 헤더 직접 설정 메서드
    private void setCookieHeader(HttpServletResponse response, String name, String value,
                                 int maxAge, String domain) {

        try {
            // 쿠키 객체를 사용하지 않고 직접 헤더를 설정
            StringBuilder cookieBuilder = new StringBuilder();
            cookieBuilder.append(name).append("=").append(value).append(";");
            cookieBuilder.append(" Path=/;");
            cookieBuilder.append(" Max-Age=").append(maxAge).append(";");

            // 도메인이 유효한 경우에만 설정
            if (domain != null && !domain.isEmpty()) {
                cookieBuilder.append(" Domain=").append(domain).append(";");
                log.info("쿠키 도메인 설정: {}", domain);
            }

            cookieBuilder.append(" HttpOnly;");
            cookieBuilder.append(" Secure;");
            cookieBuilder.append(" SameSite=None");

            // 쿠키 헤더를 직접 추가
            response.addHeader("Set-Cookie", cookieBuilder.toString());

            log.info("쿠키 설정 완료: {}", name);
        } catch (Exception e) {
            log.info("쿠키 {} 설정 중 오류: {}", name, e.getMessage(), e);
        }
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        // 세션에 저장된 원래 리디렉션 URI 가져오기, 없으면 기본값 사용
        String defaultRedirectUri = appConfig.getSiteFrontUrl() + "/login";

        // 로그인 시도 시 파라미터로 전달된 redirect_uri가 있는지 확인
        String sessionRedirectUri = (String) request.getSession().getAttribute("redirect_uri");
        String paramRedirectUri = request.getParameter("redirect_uri");

        String redirectUri = paramRedirectUri != null ? paramRedirectUri :
                sessionRedirectUri != null ? sessionRedirectUri :
                        defaultRedirectUri;

        log.info("결정된 리디렉션 URL: {}, AppConfig.getSiteFrontUrl(): {}", redirectUri, appConfig.getSiteFrontUrl());

        return redirectUri;
    }
}