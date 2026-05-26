package com.traceability.solicitudes.application.port;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepositoryPort {

    ArchivoAdjunto save(ArchivoAdjunto archivo);

    Optional<ArchivoAdjunto> findById(UUID id);

    List<ArchivoAdjunto> findBySolicitudId(UUID solicitudId);
}
