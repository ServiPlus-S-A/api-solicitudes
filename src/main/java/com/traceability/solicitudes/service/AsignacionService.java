package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.repository.AsignacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;

    public AsignacionModel guardarAsignacion(AsignacionModel asignacion) {
        return asignacionRepository.save(asignacion);
    }

    public List<AsignacionModel> obtenerPorSolicitud(Long idSolicitud) {
        return asignacionRepository.findByIdSolicitud(idSolicitud);
    }
}