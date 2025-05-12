package com.team5.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;


@Configuration
public class SwaggerConfig {
    Server server = new Server();
    {
        server.setUrl("https://api.devapi.store");
        server.setDescription("API Server");
    }

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // HTTP 타입으로 설정
                                .scheme("bearer") // Bearer 방식 적용
                                .bearerFormat("JWT") // JWT 형식 지정
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                        .addSchemas("Multipart", new Schema().type("string").format("binary"))) // Multipart 파일 업로드를 위한 스키마 추가)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(apiInfo());
    }

    private Info apiInfo() {

        return new Info()
                .title("퉁하자 API Test")
                .description("퉁하자의 api 테스트입니다.")
                .version("1.0.0");
    }
}
