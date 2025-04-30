package com.team5.backend.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "HomeController", description = "API 서버 홈")
@RestController
public class HomeController {

    @Operation(summary = "API 서버 시작페이지", description = "API 서버 시작페이지입니다. api 호출은 인증을 해주세요")
    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String home() {
        return "API 서버에 오신 걸 환영합니다.";
    }

    @Operation(summary = "세션 확인", description = "현재 사용자 세션 정보를 반환합니다.")
    @GetMapping("/session")
    public Map<String, Object> getSessionAttributes(HttpSession session) {
        Map<String, Object> sessionMap = new HashMap<>();

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            if (value != null) {
                sessionMap.put(name, value);
            }
        }

        return sessionMap;
    }
}