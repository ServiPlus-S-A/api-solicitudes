package com.traceability.solicitudes.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje.
     * @param message mensaje descriptivo del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
