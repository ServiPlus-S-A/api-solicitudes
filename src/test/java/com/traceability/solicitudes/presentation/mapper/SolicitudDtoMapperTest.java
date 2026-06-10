package com.traceability.solicitudes.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.application.command.CreateSolicitudCommand;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.presentation.dto.solicitud.CreateSolicitudRequest;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SolicitudDtoMapperTest {

    private final SolicitudDtoMapper mapper = new SolicitudDtoMapper();

    @Test
    void shouldMapRequestToCommand() {

        CreateSolicitudRequest request =
                new CreateSolicitudRequest(
                        "Titulo",
                        "Descripcion",
                        "usuario-123");

        CreateSolicitudCommand command = mapper.toCommand(request);

        assertThat(command.titulo()).isEqualTo("Titulo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.solicitanteId()).isEqualTo("usuario-123");
    }

    @Test
    void shouldMapSolicitudToResponse() {

        UUID id = UUID.randomUUID();

        Instant creado = Instant.now();
        Instant actualizado = Instant.now();

        Solicitud solicitud =
                new Solicitud(
                        id,
                        "Titulo",
                        "Descripcion",
                        "usuario-123",
                        SolicitudEstado.ENVIADA,
                        creado,
                        actualizado);

        var response = mapper.toResponse(solicitud);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.titulo()).isEqualTo("Titulo");
        assertThat(response.descripcion()).isEqualTo("Descripcion");
        assertThat(response.solicitanteId()).isEqualTo("usuario-123");
        assertThat(response.estado()).isEqualTo(SolicitudEstado.ENVIADA);
        assertThat(response.creadoEn()).isEqualTo(creado);
        assertThat(response.actualizadoEn()).isEqualTo(actualizado);
    }
}