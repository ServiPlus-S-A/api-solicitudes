package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.AsignacionModel;
import java.time.LocalDateTime;

public record AsignacionDTO(
        Long id,
        Long idSolicitud,
        Long idConsultor,
        LocalDateTime fechaAsignacion
) {
    // Mapeador de Entidad a DTO
    public static AsignacionDTO fromEntity(AsignacionModel model) {
        if (model == null) return null;
        return new AsignacionDTO(
                model.getId(),
                model.getIdSolicitud(),
                model.getIdConsultor(),
                model.getFechaAsignacion()
        );
    }

    // Mapeador de DTO a Entidad
    public AsignacionModel toEntity() {
        return AsignacionModel.builder()
                .id(this.id)
                .idSolicitud(this.idSolicitud)
                .idConsultor(this.idConsultor)
                .fechaAsignacion(this.fechaAsignacion)
                .build();
    }
}