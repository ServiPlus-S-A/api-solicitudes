package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.AdjuntoModel;

/**
 * DTO de transferencia de datos para la gestión de archivos adjuntos.
 * Representado como un Record para inmutabilidad estructural.
 *
 * @param id             Identificador único del adjunto
 * @param idSolicitud    Identificador de la solicitud relacionada
 * @param urlArchivo     Dirección de almacenamiento del archivo adjunto
 * @param tipoArchivo    Formato o extensión del archivo
 */
public record AdjuntoDTO(
        Long id,
        Long idSolicitud,
        String urlArchivo,
        String tipoArchivo
) {
    /**
     * Mapeador estático para convertir un modelo de entidad a DTO.
     *
     * @param model Entidad de persistencia original
     * @return DTO instanciado o null si el modelo de origen es nulo
     */
    public static AdjuntoDTO fromEntity(AdjuntoModel model) {
        if (model == null) {
            return null;
        }
        return new AdjuntoDTO(
                model.getId(),
                model.getSolicitud() != null ? model.getSolicitud().getId() : null,
                model.getUrlArchivo(),
                model.getTipoArchivo()
        );
    }

    /**
     * Mapeador para transformar la información del DTO a su entidad de persistencia.
     *
     * @return Instancia estructurada de AdjuntoModel
     */
    public AdjuntoModel toEntity() {
        return AdjuntoModel.builder()
                .id(this.id)
                .urlArchivo(this.urlArchivo)
                .tipoArchivo(this.tipoArchivo)
                .build();
    }
}