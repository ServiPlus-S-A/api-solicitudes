package com.traceability.solicitudes.infrastructure.persistence.inmemory;

import com.traceability.solicitudes.application.port.FileRepositoryPort;
import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryFileRepository implements FileRepositoryPort {

    private final Map<UUID, ArchivoAdjunto> store = new ConcurrentHashMap<>();

    @Override
    public ArchivoAdjunto save(ArchivoAdjunto archivo) {
        store.put(archivo.getId(), archivo);
        return archivo;
    }

    @Override
    public Optional<ArchivoAdjunto> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ArchivoAdjunto> findBySolicitudId(UUID solicitudId) {
        return store.values().stream()
                .filter(a -> a.getSolicitudId().equals(solicitudId))
                .collect(Collectors.toList());
    }
}
