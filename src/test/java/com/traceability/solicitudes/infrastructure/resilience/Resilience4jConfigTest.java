package com.traceability.solicitudes.infrastructure.resilience;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Resilience4jConfigTest {

    @Test
    void shouldCreateConfigInstance() {
        Resilience4jConfig config = new Resilience4jConfig();

        // Verificamos que se instancie correctamente para cubrir el constructor por defecto
        assertThat(config).isNotNull();
    }
}