package com.traceability.solicitudes.infrastructure.persistence.jpa.repository;

import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.ArchivoAdjuntoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface ArchivoAdjuntoJpaRepository extends JpaRepository<ArchivoAdjuntoEntity, UUID> {

    List<ArchivoAdjuntoEntity> findBySolicitudId(UUID solicitudId);
}
