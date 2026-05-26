package com.traceability.solicitudes.presentation.dto.file;

import java.time.Instant;
import java.util.UUID;

public record ArchivoResponse(
        UUID id, UUID solicitudId, String nombreArchivo, String contentType, Instant subidoEn) {
}
