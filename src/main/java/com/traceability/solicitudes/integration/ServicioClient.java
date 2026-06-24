package com.traceability.solicitudes.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

/**
 * Cliente de integración para interactuar con el microservicio de Servicios.
 */
@Component
public class ServicioClient {

    /**
     * Valida y obtiene información del tipo de servicio. Protegido por Circuit Breaker.
     */
    @CircuitBreaker(name = "solicitudService", fallbackMethod = "obtenerServicioFallback")
    public String obtenerServicio(Long idTipoServicio) {
        // Simulación de llamada HTTP remota
        return "Servicio info ID: " + idTipoServicio;
    }

    /**
     * Método de fallback en caso de fallo o circuito abierto.
     */
    public String obtenerServicioFallback(Long idTipoServicio, Throwable t) {
        log.warn("Activado fallback para servicio ID: {} debido a: {}", idTipoServicio, t.getMessage());
        return "Servicio ID: " + idTipoServicio + " (Fallback - Servicio de Catálogo fuera de servicio)";
    }
}
