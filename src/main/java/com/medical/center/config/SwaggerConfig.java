package com.medical.center.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${APP_SERVER_URL:https://medical-center-production-3e72.up.railway.app}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("Medical Center API")
                .description("Система управления медицинским центром — REST API")
                .version("1.0.0")
                .contact(new Contact().name("Medical Center").email("admin@medical-center.ru")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Введите JWT токен, полученный при входе")));

        if (serverUrl != null && !serverUrl.isBlank()) {
            openAPI.servers(List.of(new Server().url(serverUrl)));
        }

        return openAPI;
    }
}
