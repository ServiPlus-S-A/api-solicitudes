package com.traceability.solicitudes.domain.exception;

import java.util.UUID;

public class SolicitudNotFoundException extends RuntimeException {

    public SolicitudNotFoundException(UUID id) {
        super("Solicitud no encontrada: " + id);
    }
}
