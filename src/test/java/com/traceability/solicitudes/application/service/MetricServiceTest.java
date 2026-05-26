package com.traceability.solicitudes.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

    @Mock
    private SolicitudRepositoryPort repository;

    private MetricService metricService;

    @BeforeEach
    void setUp() {
        metricService = new MetricService(repository);
    }

    @Test
    void should_calculate_resumen() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByEstado(SolicitudEstado.EN_REVISION)).thenReturn(2L);
        when(repository.countByEstado(SolicitudEstado.ENVIADA)).thenReturn(3L);
        when(repository.countByEstado(SolicitudEstado.APROBADA)).thenReturn(4L);
        when(repository.countByEstado(SolicitudEstado.RECHAZADA)).thenReturn(1L);

        var resumen = metricService.obtenerResumen();

        assertThat(resumen.getTotalSolicitudes()).isEqualTo(10);
        assertThat(resumen.getSolicitudesPendientes()).isEqualTo(5);
        assertThat(resumen.getSolicitudesAprobadas()).isEqualTo(4);
        assertThat(resumen.getSolicitudesRechazadas()).isEqualTo(1);
    }
}
