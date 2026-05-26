package com.traceability.solicitudes.infrastructure.persistence.jpa.repository;

import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.AuditLogEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {
}
