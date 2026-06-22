package com.traceability.solicitudes.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Estructura estándar de error REST para las respuestas HTTP del microservicio.
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
