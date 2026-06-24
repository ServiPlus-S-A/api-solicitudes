package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.AdjuntoModel;

public record AdjuntoDTO(
        Long id,
        Long idSolicitud,
        String urlArchivo,  // Ajustado al modelo real
        String tipoArchivo  // Ajustado al modelo real
) {
    // Mapeador de Entidad a DTO
    public static AdjuntoDTO fromEntity(AdjuntoModel model) {
        if (model == null) return null;
        return new AdjuntoDTO(
                model.getId(),
                model.getIdSolicitud(),
                model.getUrlArchivo(), // Mapeo correcto
                model.getTipoArchivo()  // Mapeo correcto
        );
    }

    // Mapeador de DTO a Entidad
    public AdjuntoModel toEntity() {
        return AdjuntoModel.builder()
                .id(this.id)
                .idSolicitud(this.idSolicitud)
                .urlArchivo(this.urlArchivo) // Mapeo correcto
                .tipoArchivo(this.tipoArchivo) // Mapeo correcto
                .build();
    }
}