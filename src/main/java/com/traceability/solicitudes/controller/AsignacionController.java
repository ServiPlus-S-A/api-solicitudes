package com.traceability.solicitudes.controller;

import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;

    @PostMapping
    public ResponseEntity<AsignacionModel> crearAsignacion(@RequestBody AsignacionModel asignacion) {
        return ResponseEntity.ok(asignacionService.guardarAsignacion(asignacion));
    }

    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<AsignacionModel>> listarPorSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(asignacionService.obtenerPorSolicitud(idSolicitud));
    }
}