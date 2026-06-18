package com.traceability.solicitudes.exception;

/**
 * Excepción de negocio para indicar violaciones en las reglas de proceso.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructor con mensaje.
     * @param message mensaje descriptivo del error
     */
    public BusinessException(String message) {
        super(message);
    }
}
