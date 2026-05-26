package com.traceability.solicitudes.presentation.dto.metrics;

public record MetricaResumenResponse(
        long totalSolicitudes,
        long solicitudesPendientes,
        long solicitudesAprobadas,
        long solicitudesRechazadas) {
}
