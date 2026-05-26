package com.traceability.solicitudes.domain.service;

import com.traceability.solicitudes.domain.exception.DomainValidationException;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import org.springframework.stereotype.Component;

@Component
public class SolicitudDomainService {

    public void validateForCreate(String titulo, String descripcion, String solicitanteId) {
        if (titulo == null || titulo.isBlank()) {
            throw new DomainValidationException("El título es obligatorio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new DomainValidationException("La descripción es obligatoria");
        }
        if (solicitanteId == null || solicitanteId.isBlank()) {
            throw new DomainValidationException("El solicitante es obligatorio");
        }
    }

    public void validateStateTransition(Solicitud solicitud, SolicitudEstado nuevoEstado) {
        if (solicitud.getEstado() == SolicitudEstado.RECHAZADA
                || solicitud.getEstado() == SolicitudEstado.APROBADA) {
            throw new DomainValidationException(
                    "No se puede cambiar el estado de una solicitud terminal: " + solicitud.getEstado());
        }
        if (nuevoEstado == SolicitudEstado.BORRADOR && solicitud.getEstado() != SolicitudEstado.BORRADOR) {
            throw new DomainValidationException("No se puede volver a estado BORRADOR");
        }
    }
}
