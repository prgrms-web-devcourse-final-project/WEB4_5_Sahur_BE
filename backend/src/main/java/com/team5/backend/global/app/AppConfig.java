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
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    private String frontendUrl;
    private String backendUrl;

    @Value("${custom.site.frontUrl}")
    public void setFrontendUrl(String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    @Value("${custom.site.backUrl}")
    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    // Static methods to access the values
    public String getSiteFrontUrl() {
        return frontendUrl;
    }

    public String getSiteBackUrl() {
        return backendUrl;
    }

}