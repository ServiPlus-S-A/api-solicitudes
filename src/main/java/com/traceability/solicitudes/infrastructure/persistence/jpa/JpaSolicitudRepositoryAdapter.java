package com.traceability.solicitudes.infrastructure.persistence.jpa;

import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.SolicitudEntity;
import com.traceability.solicitudes.infrastructure.persistence.jpa.repository.SolicitudJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public class JpaSolicitudRepositoryAdapter implements SolicitudRepositoryPort {

    private final SolicitudJpaRepository jpaRepository;

    public JpaSolicitudRepositoryAdapter(SolicitudJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Solicitud save(Solicitud solicitud) {
        SolicitudEntity saved = jpaRepository.save(SolicitudEntity.fromDomain(solicitud));
        return saved.toDomain();
    }

    @Override
    public Optional<Solicitud> findById(UUID id) {
        return jpaRepository.findById(id).map(SolicitudEntity::toDomain);
    }

    @Override
    public List<Solicitud> findAll(int page, int size) {
        return jpaRepository
                .findAllByOrderByCreadoEnDesc(PageRequest.of(page, size, Sort.by("creadoEn").descending()))
                .stream()
                .map(SolicitudEntity::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByEstado(SolicitudEstado estado) {
        return jpaRepository.countByEstado(estado);
    }
}
