package com.traceability.solicitudes.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArchivoAdjuntoEntityTest {

    @Test
    void shouldMapFromDomainAndBackToDomain() {

        UUID id = UUID.randomUUID();
        UUID solicitudId = UUID.randomUUID();
        Instant fecha = Instant.now();

        ArchivoAdjunto archivo =
                new ArchivoAdjunto(
                        id,
                        solicitudId,
                        "archivo.pdf",
                        "application/pdf",
                        "storage-key",
                        fecha);

        ArchivoAdjuntoEntity entity =
                ArchivoAdjuntoEntity.fromDomain(archivo);

        ArchivoAdjunto result =
                entity.toDomain();

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(result.getNombreArchivo()).isEqualTo("archivo.pdf");
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getStorageKey()).isEqualTo("storage-key");
        assertThat(result.getSubidoEn()).isEqualTo(fecha);
    }
}