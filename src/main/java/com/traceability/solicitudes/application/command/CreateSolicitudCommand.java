package com.traceability.solicitudes.application.command;

public record CreateSolicitudCommand(String titulo, String descripcion, String solicitanteId) {
}
