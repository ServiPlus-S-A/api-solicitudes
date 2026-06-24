package com.traceability.solicitudes.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

/**
 * Cliente de integración para interactuar con el microservicio de Clientes.
 */
@Component
public class ClienteClient {

    /**
     * Valida y obtiene información de un cliente. Protegido por Circuit Breaker.
     */
    @CircuitBreaker(name = "solicitudService", fallbackMethod = "obtenerClienteFallback")
    public String obtenerCliente(Long idCliente) {
        // Simulación de llamada HTTP remota
        return "Cliente info ID: " + idCliente;
    }

    /**
     * Método de fallback en caso de fallo o circuito abierto.
     */
    public String obtenerClienteFallback(Long idCliente, Throwable t) {
        log.warn("Activado fallback para cliente ID: {} debido a: {}", idCliente, t.getMessage());
        return "Cliente ID: " + idCliente + " (Fallback - Servicio de Clientes fuera de servicio)";
    }
}
