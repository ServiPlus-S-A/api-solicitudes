package com.traceability.solicitudes.application.service;

import com.traceability.solicitudes.infrastructure.cache.CacheNames;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @CacheEvict(value = CacheNames.SOLICITUD, key = "#id")
    public void evictSolicitud(UUID id) {
        // Invalidación ADR-001 en POST/PUT/DELETE
    }

    @CacheEvict(value = CacheNames.SOLICITUD, allEntries = true)
    public void evictAllSolicitudes() {
        // Invalidación masiva tras cambios de estado globales
    }

    @CacheEvict(value = CacheNames.METRICAS, allEntries = true)
    public void evictMetricas() {
        // Invalidación ADR-001 — TTL 1 min + manual en cambio de estado
    }
}
