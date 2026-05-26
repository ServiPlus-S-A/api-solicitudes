package com.traceability.solicitudes.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainExceptionsTest {

    @Test
    void should_create_not_found_exception() {
        var ex = new SolicitudNotFoundException(UUID.randomUUID());
        assertThat(ex.getMessage()).contains("Solicitud no encontrada");
    }

    @Test
    void should_create_validation_exception() {
        var ex = new DomainValidationException("error");
        assertThat(ex.getMessage()).isEqualTo("error");
    }
}
