package com.traceability.solicitudes.presentation.dto.solicitud;

import com.traceability.solicitudes.domain.model.SolicitudEstado;
import jakarta.validation.constraints.NotNull;

public record UpdateEstadoRequest(@NotNull SolicitudEstado estado) {
}
