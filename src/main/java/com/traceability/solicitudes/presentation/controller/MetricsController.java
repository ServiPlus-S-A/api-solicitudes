package com.traceability.solicitudes.presentation.controller;

import com.traceability.solicitudes.application.service.MetricService;
import com.traceability.solicitudes.presentation.dto.metrics.MetricaResumenResponse;
import com.traceability.solicitudes.presentation.mapper.MetricsDtoMapper;
import com.traceability.solicitudes.utils.Constants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/metrics")
public class MetricsController {

    private final MetricService metricService;
    private final MetricsDtoMapper mapper;

    public MetricsController(MetricService metricService, MetricsDtoMapper mapper) {
        this.metricService = metricService;
        this.mapper = mapper;
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE')")
    public MetricaResumenResponse resumen() {
        return mapper.toResponse(metricService.obtenerResumen());
    }
}
