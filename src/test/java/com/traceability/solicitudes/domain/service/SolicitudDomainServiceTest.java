package com.traceability.solicitudes.domain.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.traceability.solicitudes.domain.exception.DomainValidationException;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolicitudDomainServiceTest {

    private SolicitudDomainService service;

    @BeforeEach
    void setUp() {
        service = new SolicitudDomainService();
    }

    @Test
    void should_pass_validation_when_create_data_is_valid() {
        assertThatCode(() -> service.validateForCreate("Titulo", "Descripcion", "user-1"))
                .doesNotThrowAnyException();
    }

    @Test
    void should_fail_when_titulo_is_blank() {
        assertThatThrownBy(() -> service.validateForCreate(" ", "Descripcion", "user-1"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("título");
    }

    @Test
    void should_fail_when_descripcion_is_blank() {
        assertThatThrownBy(() -> service.validateForCreate("Titulo", "", "user-1"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("descripción");
    }

    @Test
    void should_fail_when_solicitante_is_blank() {
        assertThatThrownBy(() -> service.validateForCreate("Titulo", "Descripcion", null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("solicitante");
    }

    @Test
    void should_fail_transition_from_terminal_state() {
        Solicitud solicitud = new Solicitud(
                UUID.randomUUID(),
                "T",
                "D",
                "u1",
                SolicitudEstado.APROBADA,
                Instant.now(),
                Instant.now());

        assertThatThrownBy(() -> service.validateStateTransition(solicitud, SolicitudEstado.EN_REVISION))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void should_fail_transition_back_to_borrador() {
        Solicitud solicitud = new Solicitud(
                UUID.randomUUID(),
                "T",
                "D",
                "u1",
                SolicitudEstado.ENVIADA,
                Instant.now(),
                Instant.now());

        assertThatThrownBy(() -> service.validateStateTransition(solicitud, SolicitudEstado.BORRADOR))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void should_allow_valid_state_transition() {
        Solicitud solicitud = new Solicitud(
                UUID.randomUUID(),
                "T",
                "D",
                "u1",
                SolicitudEstado.ENVIADA,
                Instant.now(),
                Instant.now());

        assertThatCode(() -> service.validateStateTransition(solicitud, SolicitudEstado.EN_REVISION))
                .doesNotThrowAnyException();
    }
}
