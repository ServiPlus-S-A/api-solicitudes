package com.traceability.solicitudes.controller;

import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.service.AdjuntoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/adjuntos")
@RequiredArgsConstructor
public class AdjuntoController { // <-- CORREGIDO: Ahora coincide con el archivo AdjuntoController.java

    private final AdjuntoService adjuntoService;

    @PostMapping
    public ResponseEntity<AdjuntoModel> subirAdjunto(@RequestBody AdjuntoModel adjunto) {
        return ResponseEntity.ok(adjuntoService.guardarAdjunto(adjunto));
    }

    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<AdjuntoModel>> listarPorSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(adjuntoService.obtenerPorSolicitud(idSolicitud));
    }
}