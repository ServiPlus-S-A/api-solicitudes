package com.traceability.solicitudes.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

/**
 * Servicio para recopilación y envío de métricas de negocio.
 */
@Service
public class MetricService {

    private final MeterRegistry meterRegistry;

    /**
     * Constructor con registro de métricas.
     * @param meterRegistry registro de micrometer
     */
    public MetricService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Incrementa un contador de métrica.
     * @param nombreMetrica nombre
     * @param tags etiquetas
     */
    public void incrementarContador(String nombreMetrica, String... tags) {
        meterRegistry.counter(nombreMetrica, tags).increment();
    }
}
