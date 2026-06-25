package com.traceability.solicitudes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.time.LocalDateTime;

/**
 * DTO para enviar la respuesta de solicitudes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResponseDTO {
    private Long id;
    private Long idCliente;
    private Long idTipoServicio;
    private String asunto;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaApertura;
    private String codigoTrazabilidad;
    private String ubicacion;
    private List<String> urlsAdjuntos;
}
