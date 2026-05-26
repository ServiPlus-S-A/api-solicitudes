package com.traceability.solicitudes.infrastructure.persistence.jpa;

import com.traceability.solicitudes.application.port.AuditLogPort;
import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.AuditLogEntity;
import com.traceability.solicitudes.infrastructure.persistence.jpa.repository.AuditLogJpaRepository;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("jpa")
public class JpaAuditLogAdapter implements AuditLogPort {

    private final AuditLogJpaRepository repository;

    public JpaAuditLogAdapter(AuditLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void record(String action, String userId, Map<String, String> metadata) {
        repository.save(new AuditLogEntity(action, userId, Map.copyOf(metadata), Instant.now()));
    }
}
