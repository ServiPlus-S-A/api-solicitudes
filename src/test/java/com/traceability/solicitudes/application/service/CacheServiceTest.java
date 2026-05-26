package com.traceability.solicitudes.application.service;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }

    @Test
    void should_evict_solicitud_without_error() {
        assertThatCode(() -> cacheService.evictSolicitud(UUID.randomUUID())).doesNotThrowAnyException();
    }

    @Test
    void should_evict_all_solicitudes_without_error() {
        assertThatCode(cacheService::evictAllSolicitudes).doesNotThrowAnyException();
    }

    @Test
    void should_evict_metricas_without_error() {
        assertThatCode(cacheService::evictMetricas).doesNotThrowAnyException();
    }
}
