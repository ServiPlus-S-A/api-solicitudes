package com.traceability.solicitudes.domain.model;

public class MetricaResumen {

    private final long totalSolicitudes;
    private final long solicitudesPendientes;
    private final long solicitudesAprobadas;
    private final long solicitudesRechazadas;

    public MetricaResumen(
            long totalSolicitudes,
            long solicitudesPendientes,
            long solicitudesAprobadas,
            long solicitudesRechazadas) {
        this.totalSolicitudes = totalSolicitudes;
        this.solicitudesPendientes = solicitudesPendientes;
        this.solicitudesAprobadas = solicitudesAprobadas;
        this.solicitudesRechazadas = solicitudesRechazadas;
    }

    public long getTotalSolicitudes() {
        return totalSolicitudes;
    }

    public long getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public long getSolicitudesAprobadas() {
        return solicitudesAprobadas;
    }

    public long getSolicitudesRechazadas() {
        return solicitudesRechazadas;
    }
}
