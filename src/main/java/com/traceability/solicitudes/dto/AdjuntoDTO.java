package com.traceability.solicitudes.dto;

import com.traceability.solicitudes.model.AdjuntoModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

        @NotNull(message = "El identificador de la solicitud (idSolicitud) es obligatorio")
        Long idSolicitud,

        @NotBlank(message = "La URL del archivo (urlArchivo) no puede estar vacía")
        @Size(max = 255, message = "La URL del archivo no puede superar los 255 caracteres")
        String urlArchivo,

        @Size(max = 10, message = "El tipo de archivo no puede superar los 10 caracteres")
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