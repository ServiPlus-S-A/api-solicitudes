package com.traceability.solicitudes.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI solicitudesOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TraceAbility — Microservicio Solicitudes")
                        .description("API REST para gestión de solicitudes (Sprint 0)")
                        .version(ApiVersions.V1));
    }
}
