package com.traceability.solicitudes.infrastructure.web;

import com.traceability.solicitudes.domain.exception.DomainValidationException;
import com.traceability.solicitudes.domain.exception.SolicitudNotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SolicitudNotFoundException.class)
    public ProblemDetail handleNotFound(SolicitudNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Recurso no encontrado");
        detail.setProperty("timestamp", Instant.now().toString());
        return detail;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleValidation(DomainValidationException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Validación de dominio");
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBeanValidation(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
        detail.setProperty(
                "errors",
                ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                        .toList());
        return detail;
    }
}
