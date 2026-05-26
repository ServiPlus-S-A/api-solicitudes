package com.traceability.solicitudes.application.port;

import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudRepositoryPort {

    Solicitud save(Solicitud solicitud);

    Optional<Solicitud> findById(UUID id);

    List<Solicitud> findAll(int page, int size);

    long count();

    long countByEstado(SolicitudEstado estado);
}
