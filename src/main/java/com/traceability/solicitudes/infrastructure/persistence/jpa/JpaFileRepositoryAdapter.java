package com.traceability.solicitudes.infrastructure.persistence.jpa;

import com.traceability.solicitudes.application.port.FileRepositoryPort;
import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.ArchivoAdjuntoEntity;
import com.traceability.solicitudes.infrastructure.persistence.jpa.repository.ArchivoAdjuntoJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public class JpaFileRepositoryAdapter implements FileRepositoryPort {

    private final ArchivoAdjuntoJpaRepository jpaRepository;

    public JpaFileRepositoryAdapter(ArchivoAdjuntoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ArchivoAdjunto save(ArchivoAdjunto archivo) {
        return jpaRepository.save(ArchivoAdjuntoEntity.fromDomain(archivo)).toDomain();
    }

    @Override
    public Optional<ArchivoAdjunto> findById(UUID id) {
        return jpaRepository.findById(id).map(ArchivoAdjuntoEntity::toDomain);
    }

    @Override
    public List<ArchivoAdjunto> findBySolicitudId(UUID solicitudId) {
        return jpaRepository.findBySolicitudId(solicitudId).stream()
                .map(ArchivoAdjuntoEntity::toDomain)
                .toList();
    }
}
