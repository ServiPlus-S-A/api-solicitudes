package com.traceability.solicitudes.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FileDtoMapperTest {

    private final FileDtoMapper mapper = new FileDtoMapper();

    @Test
    void shouldMapArchivoToResponse() {
        UUID archivoId = UUID.randomUUID();
        UUID solicitudId = UUID.randomUUID();
        Instant fecha = Instant.now();

        ArchivoAdjunto archivo =
                new ArchivoAdjunto(
                        archivoId,
                        solicitudId,
                        "evidencia.pdf",
                        "application/pdf",
                        "s3/evidencia.pdf",
                        fecha);

        var response = mapper.toResponse(archivo);

        assertThat(response.id()).isEqualTo(archivoId);
        assertThat(response.solicitudId()).isEqualTo(solicitudId);
        assertThat(response.nombreArchivo()).isEqualTo("evidencia.pdf");
        assertThat(response.contentType()).isEqualTo("application/pdf");
        assertThat(response.subidoEn()).isEqualTo(fecha);
    }
}