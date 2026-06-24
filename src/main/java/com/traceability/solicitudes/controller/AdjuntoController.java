package com.traceability.solicitudes.controller;

import com.traceability.solicitudes.dto.AdjuntoDTO;
import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.service.AdjuntoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Controlador REST encargado de exponer los endpoints de archivos adjuntos.
 */
@RestController
@RequestMapping("/api/adjuntos")
@RequiredArgsConstructor
@Tag(name = "Adjuntos", description =
        "Operaciones para la gestión aislada de archivos vinculados a las solicitudes")
public class AdjuntoController {

    private final AdjuntoService adjuntoService;

    /**
     * Endpoint HTTP POST para cargar y guardar un adjunto usando DTOs.
     * @param adjuntoDto el DTO enviado en el JSON por el cliente
     * @return el DTO del adjunto guardado con estado HTTP 200
     */
    @PostMapping
    @Operation(summary = "Cargar un nuevo adjunto",
            description = "Registra un archivo adjunto aislado vinculándolo a una solicitud")
    @ApiResponse(responseCode = "201", description = "Adjunto creado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o fallos de validación")
    public ResponseEntity<AdjuntoDTO> subirAdjunto(@RequestBody AdjuntoDTO adjuntoDto) {
        // 1. Convertimos la instancia 'adjuntoDto' que llegó por HTTP a Entidad
        AdjuntoModel entidad = adjuntoDto.toEntity();

        // 2. Pasamos la entidad al servicio para que se guarde
        AdjuntoModel guardado = adjuntoService.guardarAdjunto(entidad);

        // 3. Devolvemos el resultado transformado de nuevo a DTO
        return new ResponseEntity<>(AdjuntoDTO.fromEntity(guardado), HttpStatus.CREATED);
    }

    /**
     * Endpoint HTTP GET para recuperar los adjuntos de una solicitud.
     * @param idSolicitud identificador de la solicitud desde la URL
     * @return lista de adjuntos transformados a DTOs
     */
    @GetMapping("/solicitud/{idSolicitud}")
    @Operation(summary = "Listar adjuntos por solicitud",
            description = "Obtiene de forma aislada todos los adjuntos de una solicitud específica")
    @ApiResponse(responseCode = "200", description = "Lista de adjuntos devuelta exitosamente")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<List<AdjuntoDTO>> listarPorSolicitud(@PathVariable Long idSolicitud) {
        // Obtenemos las entidades del servicio y las convertimos todas a DTOs usando Streams
        List<AdjuntoDTO> dtos = adjuntoService.obtenerPorSolicitud(idSolicitud)
                .stream()
                .map(AdjuntoDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}