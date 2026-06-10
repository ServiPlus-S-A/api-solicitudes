package com.traceability.solicitudes.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.exception.DomainValidationException;
import com.traceability.solicitudes.domain.exception.SolicitudNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleNotFoundException() {
        // Generamos un UUID real porque la excepción lo exige obligatoriamente
        UUID idPrueba = UUID.randomUUID();

        ProblemDetail detail = handler.handleNotFound(
                new SolicitudNotFoundException(idPrueba)
        );

        assertThat(detail.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(detail.getTitle())
                .isEqualTo("Recurso no encontrado");

        // Verificamos que el detalle contenga el UUID para que el test sea robusto
        assertThat(detail.getDetail())
                .contains(idPrueba.toString());

        assertThat(detail.getProperties())
                .containsKey("timestamp");
    }

    @Test
    void shouldHandleDomainValidationException() {
        ProblemDetail detail = handler.handleValidation(
                new DomainValidationException("Inválido")
        );

        assertThat(detail.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());

        assertThat(detail.getTitle())
                .isEqualTo("Validación de dominio");

        assertThat(detail.getDetail())
                .isEqualTo("Inválido");
    }
}