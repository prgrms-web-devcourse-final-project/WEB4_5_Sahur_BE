package com.team5.backend.global.handler;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.global.security.PrincipalDetails;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        // JWT 토큰 생성 (JwtUtil 사용)
        String accessToken = jwtUtil.generateAccessToken(
                member.getMemberId(),
                member.getEmail(),
                member.getRole().name()
        );

        // JWT 리프레시 토큰 생성
        String refreshToken = jwtUtil.generateRefreshToken(
                member.getMemberId(),
                member.getEmail(),
                member.getRole().name()
        );

        // 쿠키 만료 시간을 JwtUtil에서 관리하는 토큰 만료 시간과 동일하게 설정
        addCookie(response, "accessToken", accessToken, (int)(jwtUtil.getAccessTokenExpiration() / 1000));
        addCookie(response, "refreshToken", refreshToken, (int)(jwtUtil.getRefreshTokenExpiration() / 1000));

        // 리디렉션 URI 가져오기 (클라이언트에서 전달한 redirect_uri 파라미터 값)
        String targetUrl = determineTargetUrl(request, response, authentication);

        // 리디렉션 URI에 토큰 추가
        if (targetUrl.contains("?")) {
            targetUrl += "&accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        } else {
            targetUrl += "?accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        }

        // 리디렉션 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        // 세션에 저장된 원래 리디렉션 URI 가져오기, 없으면 기본값 사용
        String defaultRedirectUri = "http://localhost:3000/member/login";
        String redirectUri = request.getParameter("redirect_uri");

        return redirectUri != null ? redirectUri : defaultRedirectUri;
    }

    // 쿠키 생성 메서드 (만료 시간 설정)
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");
        cookie.setMaxAge(maxAge); // 만료 시간 설정
        cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가
        cookie.setSecure(true); // HTTPS 환경에서만 사용 가능

        response.addCookie(cookie);
    }
}
