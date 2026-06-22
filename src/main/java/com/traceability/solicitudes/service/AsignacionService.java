package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.repository.AsignacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para el manejo de asignaciones de solicitudes.
 */
@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;

    /**
     * Registra y guarda una nueva asignación en el sistema.
     * @param asignacion el modelo de asignación a persistir
     * @return la asignación guardada con los datos del sistema
     */
    public AsignacionModel guardarAsignacion(AsignacionModel asignacion) {
        return asignacionRepository.save(asignacion);
    }

    /**
     * Recupera el histórico de asignaciones que ha tenido una solicitud.
     * @param idSolicitud identificador único de la solicitud
     * @return lista de asignaciones encontradas
     */
    public List<AsignacionModel> obtenerPorSolicitud(Long idSolicitud) {
        return asignacionRepository.findByIdSolicitud(idSolicitud);
    }
}