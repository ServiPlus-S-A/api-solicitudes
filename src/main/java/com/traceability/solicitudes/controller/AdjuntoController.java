package com.traceability.solicitudes.controller;

import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.service.AdjuntoService;
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
 * Controlador REST encargado de exponer los endpoints de archivos adjuntos.
 */
@RestController
@RequestMapping("/api/adjuntos")
@RequiredArgsConstructor
public class AdjuntoController {

    private final AdjuntoService adjuntoService;

    /**
     * Endpoint HTTP POST para cargar y guardar un adjunto.
     * @param adjunto el modelo del adjunto enviado en el JSON
     * @return entidad con el adjunto guardado y estado HTTP 200
     */
    @PostMapping
    public ResponseEntity<AdjuntoModel> subirAdjunto(@RequestBody AdjuntoModel adjunto) {
        return ResponseEntity.ok(adjuntoService.guardarAdjunto(adjunto));
    }

    /**
     * Endpoint HTTP GET para recuperar los adjuntos de una solicitud.
     * @param idSolicitud identificador de la solicitud desde la URL
     * @return entidad con la lista de adjuntos encontrados
     */
    @GetMapping("/solicitud/{idSolicitud}")
    public ResponseEntity<List<AdjuntoModel>> listarPorSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(adjuntoService.obtenerPorSolicitud(idSolicitud));
    }
}