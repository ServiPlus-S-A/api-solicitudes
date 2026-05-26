package com.traceability.solicitudes.presentation.dto.solicitud;

import com.traceability.solicitudes.domain.model.SolicitudEstado;
import java.time.Instant;
import java.util.UUID;

public record SolicitudResponse(
        UUID id,
        String titulo,
        String descripcion,
        String solicitanteId,
        SolicitudEstado estado,
        Instant creadoEn,
        Instant actualizadoEn) {
}
