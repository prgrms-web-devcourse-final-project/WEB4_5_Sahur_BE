package com.team5.backend.global.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public static boolean isNotProd() {
        return true;
    }

    @Getter
    public static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    @Value("${custom.site.frontUrl}")
    private static String frontendUrl;

    @Value("${custom.site.backUrl}")
    private static String backendUrl;

    public static String getSiteFrontUrl() {
        return frontendUrl;
    }

    public static String getSiteBackUrl() {
        return backendUrl;
    }

}