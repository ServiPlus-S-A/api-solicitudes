package com.traceability.solicitudes.controller;

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
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    /**
     * Endpoint HTTP POST para realizar una nueva asignación.
     * @param asignacion el modelo de la asignación enviado en el JSON
     * @return entidad con la asignación creada
     */
    @PostMapping
    public ResponseEntity<AsignacionModel> crearAsignacion(@RequestBody AsignacionModel asignacion) {
        return ResponseEntity.ok(asignacionService.guardarAsignacion(asignacion));
    }

    /**
     * Endpoint HTTP GET para auditar las asignaciones que ha recibido una solicitud.
     * @param idSolicitud identificador de la solicitud desde la URL
     * @return entidad con el listado de asignaciones asociadas
     */
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<AsignacionModel>> listarPorSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(asignacionService.obtenerPorSolicitud(idSolicitud));
    }
}