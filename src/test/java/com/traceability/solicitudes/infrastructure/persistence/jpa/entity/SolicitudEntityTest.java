package com.traceability.solicitudes.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SolicitudEntityTest {

    @Test
    void shouldMapFromDomainAndBackToDomain() {

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

        SolicitudEntity entity =
                SolicitudEntity.fromDomain(solicitud);

        Solicitud result =
                entity.toDomain();

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTitulo()).isEqualTo("Titulo");
        assertThat(result.getDescripcion()).isEqualTo("Descripcion");
        assertThat(result.getSolicitanteId()).isEqualTo("usuario-123");
        assertThat(result.getEstado()).isEqualTo(SolicitudEstado.ENVIADA);
        assertThat(result.getCreadoEn()).isEqualTo(creado);
        assertThat(result.getActualizadoEn()).isEqualTo(actualizado);
    }

    @Test
    void shouldExposeGetters() {

        UUID id = UUID.randomUUID();
        Instant creado = Instant.now();
        Instant actualizado = Instant.now();

        SolicitudEntity entity =
                new SolicitudEntity(
                        id,
                        "Titulo",
                        "Descripcion",
                        "usuario-123",
                        SolicitudEstado.APROBADA,
                        creado,
                        actualizado);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitulo()).isEqualTo("Titulo");
        assertThat(entity.getDescripcion()).isEqualTo("Descripcion");
        assertThat(entity.getSolicitanteId()).isEqualTo("usuario-123");
        assertThat(entity.getEstado()).isEqualTo(SolicitudEstado.APROBADA);
        assertThat(entity.getCreadoEn()).isEqualTo(creado);
        assertThat(entity.getActualizadoEn()).isEqualTo(actualizado);
    }
}