package com.traceability.solicitudes.infrastructure.persistence.inmemory;

import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemorySolicitudRepository implements SolicitudRepositoryPort {

    private final Map<UUID, Solicitud> store = new ConcurrentHashMap<>();

    @Override
    public Solicitud save(Solicitud solicitud) {
        store.put(solicitud.getId(), solicitud);
        return solicitud;
    }

    @Override
    public Optional<Solicitud> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Solicitud> findAll(int page, int size) {
        return store.values().stream()
                .sorted(Comparator.comparing(Solicitud::getCreadoEn).reversed())
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public long countByEstado(SolicitudEstado estado) {
        return store.values().stream().filter(s -> s.getEstado() == estado).count();
    }
}
