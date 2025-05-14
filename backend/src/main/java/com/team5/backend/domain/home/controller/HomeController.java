package com.team5.backend.domain.home.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "HomeController", description = "API 서버 홈")
@RestController
public class HomeController {

    @Operation(summary = "API 서버 시작페이지", description = "API 서버 시작페이지입니다.")
    @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String home() {
        String hostName;
        try {
            // 현재 local의 인터넷 정보 획득
            InetAddress localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName() + "(" + localHost.getHostAddress() + ")";
        } catch (UnknownHostException e) {
            hostName = "알 수 없음";
        }

        // 무중단 배포 시 환경 변화 확인
        return "API 서버에 오신 걸 환영합니다. Host: " + hostName;
    }

    @GetMapping("/session")
    @ResponseBody
    public Map<String, Object> session(HttpSession session) {
        Map<String, Object> sessionMap = new HashMap<>();
        Enumeration<String> names = session.getAttributeNames();

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Object value = session.getAttribute(name);
            if (value != null) {
                sessionMap.put(name, value);
            }
        }

        return sessionMap;
    }
}