package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.SolicitudModel;
import org.springframework.stereotype.Component;

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
        return SolicitudModel.builder()
                .idCliente(dto.getIdCliente())
                .idTipoServicio(dto.getIdTipoServicio())
                .asunto(dto.getAsunto())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado() != null ? dto.getEstado() : "Pendiente")
                // 🚀 OJO: Si agregaste 'ubicacion' en tu SolicitudRequestDTO, descomenta la siguiente línea:
                // .ubicacion(dto.getUbicacion())
                // ❌ SE QUITA: .urlAdjunto(dto.getUrlAdjunto()) ya no va aquí
                .build();
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
                .estado(entity.getEstado())
                .fechaApertura(entity.getFechaApertura())
                .codigoTrazabilidad(entity.getCodigoTrazabilidad())
                // 🚀 OJO: Si tu SolicitudResponseDTO manx|eja 'ubicacion', descomenta la siguiente línea:
                // .ubicacion(entity.getUbicacion())
                // ❌ SE QUITA: .urlAdjunto(entity.getUrlAdjunto()) ya no va aquí
                .build();
    }
}