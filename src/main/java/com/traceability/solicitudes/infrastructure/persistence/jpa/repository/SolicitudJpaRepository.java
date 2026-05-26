package com.traceability.solicitudes.infrastructure.persistence.jpa.repository;

import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.SolicitudEntity;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface SolicitudJpaRepository extends JpaRepository<SolicitudEntity, UUID> {

    long countByEstado(SolicitudEstado estado);

    Page<SolicitudEntity> findAllByOrderByCreadoEnDesc(Pageable pageable);
}
