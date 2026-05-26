package com.traceability.solicitudes.presentation.mapper;

import com.traceability.solicitudes.application.command.CreateSolicitudCommand;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.presentation.dto.solicitud.CreateSolicitudRequest;
import com.traceability.solicitudes.presentation.dto.solicitud.SolicitudResponse;
import org.springframework.stereotype.Component;

@Component
public class SolicitudDtoMapper {

    public CreateSolicitudCommand toCommand(CreateSolicitudRequest request) {
        return new CreateSolicitudCommand(request.titulo(), request.descripcion(), request.solicitanteId());
    }

    public SolicitudResponse toResponse(Solicitud solicitud) {
        return new SolicitudResponse(
                solicitud.getId(),
                solicitud.getTitulo(),
                solicitud.getDescripcion(),
                solicitud.getSolicitanteId(),
                solicitud.getEstado(),
                solicitud.getCreadoEn(),
                solicitud.getActualizadoEn());
    }
}
