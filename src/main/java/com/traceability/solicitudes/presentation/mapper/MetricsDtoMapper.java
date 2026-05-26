package com.traceability.solicitudes.presentation.mapper;

import com.traceability.solicitudes.domain.model.MetricaResumen;
import com.traceability.solicitudes.presentation.dto.metrics.MetricaResumenResponse;
import org.springframework.stereotype.Component;

@Component
public class MetricsDtoMapper {

    public MetricaResumenResponse toResponse(MetricaResumen metrica) {
        return new MetricaResumenResponse(
                metrica.getTotalSolicitudes(),
                metrica.getSolicitudesPendientes(),
                metrica.getSolicitudesAprobadas(),
                metrica.getSolicitudesRechazadas());
    }
}
