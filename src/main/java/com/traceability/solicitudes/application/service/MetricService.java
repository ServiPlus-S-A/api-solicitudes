package com.traceability.solicitudes.application.service;

import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.model.MetricaResumen;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.infrastructure.cache.CacheNames;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MetricService {

    private final SolicitudRepositoryPort solicitudRepository;

    public MetricService(SolicitudRepositoryPort solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Cacheable(value = CacheNames.METRICAS, key = "'resumen'")
    public MetricaResumen obtenerResumen() {
        long total = solicitudRepository.count();
        long pendientes = solicitudRepository.countByEstado(SolicitudEstado.EN_REVISION)
                + solicitudRepository.countByEstado(SolicitudEstado.ENVIADA);
        long aprobadas = solicitudRepository.countByEstado(SolicitudEstado.APROBADA);
        long rechazadas = solicitudRepository.countByEstado(SolicitudEstado.RECHAZADA);
        return new MetricaResumen(total, pendientes, aprobadas, rechazadas);
    }
}
