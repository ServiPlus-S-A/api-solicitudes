package com.traceability.solicitudes.service;

import com.traceability.solicitudes.exception.ResourceNotFoundException;
import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.repository.AdjuntoRepository;
import com.traceability.solicitudes.repository.SolicitudRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la gestión y persistencia de archivos adjuntos.
 */
@Service
@RequiredArgsConstructor
public class AdjuntoService {

    private final AdjuntoRepository adjuntoRepository;
    private final SolicitudRepository solicitudRepository;

    /**
     * Guarda un archivo adjunto en la base de datos.
     * @param adjunto el modelo del archivo adjunto a guardar
     * @return el archivo adjunto guardado con su respectivo ID
     */
    @Transactional
    public AdjuntoModel guardarAdjunto(AdjuntoModel adjunto, Long idSolicitud) {
        SolicitudModel solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la solicitud con ID: " + idSolicitud));

        // Usamos el método helper que ya creaste en tu modelo
        solicitud.addAdjunto(adjunto);

        // Guardamos (al ser CascadeType.ALL, guardar la solicitud suele bastar,
        // pero guardar el adjunto directamente también es válido)
        return adjuntoRepository.save(adjunto);
    }

    /**
     * Obtiene todos los archivos adjuntos asociados a una solicitud específica.
     * @param idSolicitud identificador único de la solicitud
     * @return lista de archivos adjuntos relacionados
     */
    @Transactional(readOnly = true)
    public List<AdjuntoModel> obtenerPorSolicitud(Long idSolicitud) {
        return adjuntoRepository.findBySolicitudId(idSolicitud);
    }
}