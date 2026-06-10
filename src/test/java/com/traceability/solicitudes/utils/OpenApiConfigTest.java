package com.traceability.solicitudes.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void shouldCreateOpenApiConfiguration() {
        OpenAPI openApi = config.solicitudesOpenApi();

        assertThat(openApi).isNotNull();
        assertThat(openApi.getInfo()).isNotNull();
        assertThat(openApi.getInfo().getTitle()).contains("TraceAbility");
        assertThat(openApi.getInfo().getVersion()).isEqualTo(ApiVersions.V1);
    }
}