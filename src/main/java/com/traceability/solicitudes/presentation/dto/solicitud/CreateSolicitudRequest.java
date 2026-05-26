package com.traceability.solicitudes.presentation.dto.solicitud;

import jakarta.validation.constraints.NotBlank;

public record CreateSolicitudRequest(
        @NotBlank String titulo, @NotBlank String descripcion, @NotBlank String solicitanteId) {
}
