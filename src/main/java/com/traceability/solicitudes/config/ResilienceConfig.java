package com.traceability.solicitudes.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de listeners para los eventos de Resilience4J.
 */
@Configuration
public class ResilienceConfig {

    private static final Logger log = LoggerFactory.getLogger(ResilienceConfig.class);

    /**
     * Registra un listener de eventos del Circuit Breaker.
     * @param registry registro de circuit breakers de Resilience4j
     */
    public ResilienceConfig(CircuitBreakerRegistry registry) {
        registry.getEventPublisher().onEvent(event -> 
            log.info("Evento del Circuit Breaker: {}", event)
        );
    }
}
