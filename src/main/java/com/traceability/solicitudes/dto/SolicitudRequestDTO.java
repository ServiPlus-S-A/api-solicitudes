package com.traceability.solicitudes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir el payload de creación y edición de solicitudes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRequestDTO {

    @NotNull(message = "El id del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El id del tipo de servicio es obligatorio")
    private Long idTipoServicio;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 100, message = "El asunto no debe superar los 100 caracteres")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Size(max = 20, message = "El estado no debe superar los 20 caracteres")
    private String estado;

    private String urlAdjunto;
}
