package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.AsignacionModel;
import java.time.LocalDateTime;

/**
 * DTO para la transferencia de asignaciones de solicitudes a consultores.
 *
 * @param id               Identificador único de la asignación
 * @param idSolicitud      Identificador de la solicitud asignada
 * @param idConsultor      Identificador del consultor encargado
 * @param fechaAsignacion  Fecha y hora exacta del registro del cambio
 */
public record AsignacionDTO(
        Long id,
        Long idSolicitud,
        Long idConsultor,
        LocalDateTime fechaAsignacion
) {
    /**
     * Convierte una entidad AsignacionModel a su correspondiente DTO.
     *
     * @param model Entidad original de la base de datos
     * @return Instancia mapeada de AsignacionDTO o null si el modelo es nulo
     */
    public static AsignacionDTO fromEntity(AsignacionModel model) {
        if (model == null) {
            return null;
        }
        return new AsignacionDTO(
                model.getId(),
                model.getIdSolicitud(),
                model.getIdConsultor(),
                model.getFechaAsignacion()
        );
    }

    /**
     * Convierte este DTO de vuelta a una entidad AsignacionModel utilizable por JPA.
     *
     * @return Entidad de base de datos AsignacionModel estructurada
     */
    public AsignacionModel toEntity() {
        return AsignacionModel.builder()
                .id(this.id)
                .idSolicitud(this.idSolicitud)
                .idConsultor(this.idConsultor)
                .fechaAsignacion(this.fechaAsignacion)
                .build();
    }
}