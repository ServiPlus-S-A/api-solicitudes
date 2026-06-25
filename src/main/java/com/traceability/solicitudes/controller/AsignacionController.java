package com.traceability.solicitudes.controller;

import com.traceability.solicitudes.dto.AsignacionDTO;
import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST encargado de gestionar los endpoints de asignaciones.
 */
@RestController
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    /**
     * Endpoint HTTP POST para realizar una nueva asignación usando DTOs.
     * @param asignacionDto el DTO de la asignación enviado en el JSON
     * @return el DTO de la asignación creada con estado HTTP 200
     */
    @PostMapping
    public ResponseEntity<AsignacionDTO> crearAsignacion(@RequestBody AsignacionDTO asignacionDto) {
        // Transformamos el DTO de entrada a entidad para el negocio
        AsignacionModel entidad = asignacionDto.toEntity();
        AsignacionModel guardada = asignacionService.guardarAsignacion(entidad);

        // Retornamos mapeado a DTO
        return ResponseEntity.ok(AsignacionDTO.fromEntity(guardada));
    }

    /**
     * Endpoint HTTP GET para auditar las asignaciones que ha recibido una solicitud.
     * @param idSolicitud identificador de la solicitud desde la URL
     * @return listado de asignaciones asociadas transformadas a DTOs
     */
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<AsignacionDTO>> listarPorSolicitud(@PathVariable Long idSolicitud) {
        // Convertimos la lista de entidades que viene del servicio a DTOs
        List<AsignacionDTO> dtos = asignacionService.obtenerPorSolicitud(idSolicitud)
                .stream()
                .map(AsignacionDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}