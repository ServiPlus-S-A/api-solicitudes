package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.repository.AdjuntoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdjuntoService {

    private final AdjuntoRepository adjuntoRepository;

    public AdjuntoModel guardarAdjunto(AdjuntoModel adjunto) {
        return adjuntoRepository.save(adjunto);
    }

    public List<AdjuntoModel> obtenerPorSolicitud(Long idSolicitud) {
        return adjuntoRepository.findByIdSolicitud(idSolicitud);
    }
}