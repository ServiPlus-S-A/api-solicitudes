package com.traceability.solicitudes.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.model.MetricaResumen;
import org.junit.jupiter.api.Test;

class MetricsDtoMapperTest {

    private final MetricsDtoMapper mapper = new MetricsDtoMapper();

    @Test
    void shouldMapMetricaResumenToResponse() {

        MetricaResumen metrica =
                new MetricaResumen(
                        100,
                        20,
                        70,
                        10);

        var response = mapper.toResponse(metrica);

        assertThat(response.totalSolicitudes()).isEqualTo(100);
        assertThat(response.solicitudesPendientes()).isEqualTo(20);
        assertThat(response.solicitudesAprobadas()).isEqualTo(70);
        assertThat(response.solicitudesRechazadas()).isEqualTo(10);
    }
}