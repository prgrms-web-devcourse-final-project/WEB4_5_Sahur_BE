package com.team5.backend.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class CookieRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customCookies;

    public CookieRequestWrapper(HttpServletRequest request, Map<String, String> customCookies) {
        super(request);
        this.customCookies = customCookies;
    }

    @Override
    public Cookie[] getCookies() {

        Cookie[] originalCookies = super.getCookies();
        List<Cookie> modifiedCookies = new ArrayList<>();

        // 기존 쿠키 추가 (customCookies에 없는 것만)
        if (originalCookies != null) {
            for (Cookie cookie : originalCookies) {
                if (!customCookies.containsKey(cookie.getName())) {
                    modifiedCookies.add(cookie);
                }
            }
        }

        // 새 쿠키 추가
        for (Map.Entry<String, String> entry : customCookies.entrySet()) {
            Cookie newCookie = new Cookie(entry.getKey(), entry.getValue());
            modifiedCookies.add(newCookie);
        }

        return modifiedCookies.toArray(new Cookie[0]);
    }

    @Override
    public String getHeader(String name) {

        // Cookie 헤더를 요청하는 경우 수정된 쿠키 값으로 반환
        if ("cookie".equalsIgnoreCase(name)) {
            StringBuilder cookieHeader = new StringBuilder();
            Cookie[] cookies = getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());
                    if (i < cookies.length - 1) {
                        cookieHeader.append("; ");
                    }
                }
            }
            return cookieHeader.toString();
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {

        // Cookie 헤더에 대해 수정된 값 반환
        if ("cookie".equalsIgnoreCase(name)) {
            List<String> values = new ArrayList<>();
            values.add(getHeader(name));
            return Collections.enumeration(values);
        }
        return super.getHeaders(name);
    }
}
