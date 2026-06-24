package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.EstadoSolicitud;
import com.traceability.solicitudes.model.SolicitudModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapeador entre DTOs y Entidad de Solicitudes.
 */
@Component
public class SolicitudMapper {

    /**
     * Mapea un DTO de solicitud a una entidad de modelo.
     * @param dto request DTO
     * @return entidad JPA
     */
    public SolicitudModel toEntity(SolicitudRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(dto.getIdCliente())
                .idTipoServicio(dto.getIdTipoServicio())
                .asunto(dto.getAsunto())
                .descripcion(dto.getDescripcion())
                .ubicacion(dto.getUbicacion())
                .estado(dto.getEstado() != null ?
                        EstadoSolicitud.valueOf(dto.getEstado().toUpperCase()) :
                        EstadoSolicitud.PENDIENTE)
                .build();

        if (dto.getAdjuntos() != null) {
            dto.getAdjuntos().forEach(adjunto -> solicitud.addAdjunto(adjunto.toEntity()));
        }
        return solicitud;
    }

    /**
     * Mapea una entidad a un DTO de respuesta.
     * @param entity entidad JPA
     * @return response DTO
     */
    public SolicitudResponseDTO toResponse(SolicitudModel entity) {
        if (entity == null) {
            return null;
        }
        return SolicitudResponseDTO.builder()
                .id(entity.getId())
                .idCliente(entity.getIdCliente())
                .idTipoServicio(entity.getIdTipoServicio())
                .asunto(entity.getAsunto())
                .descripcion(entity.getDescripcion())
                .estado(entity.getEstado() != null ? entity.getEstado().name() : "PENDIENTE")
                .fechaApertura(entity.getFechaApertura())
                .codigoTrazabilidad(entity.getCodigoTrazabilidad())
                .ubicacion(entity.getUbicacion())
                .urlsAdjuntos(entity.getAdjuntos() !=null ?
                        entity.getAdjuntos().stream().map(adj -> adj.getUrlArchivo()).toList() :
                        List.of())
                .build();


    }
}