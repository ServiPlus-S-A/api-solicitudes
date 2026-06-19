package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.repository.AdjuntoRepository;
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

    /**
     * Guarda un archivo adjunto en la base de datos.
     * @param adjunto el modelo del archivo adjunto a guardar
     * @return el archivo adjunto guardado con su respectivo ID
     */
    public AdjuntoModel guardarAdjunto(AdjuntoModel adjunto) {
        return adjuntoRepository.save(adjunto);
    }

    /**
     * Obtiene todos los archivos adjuntos asociados a una solicitud específica.
     * @param idSolicitud identificador único de la solicitud
     * @return lista de archivos adjuntos relacionados
     */
    public List<AdjuntoModel> obtenerPorSolicitud(Long idSolicitud) {
        return adjuntoRepository.findByIdSolicitud(idSolicitud);
    }
}