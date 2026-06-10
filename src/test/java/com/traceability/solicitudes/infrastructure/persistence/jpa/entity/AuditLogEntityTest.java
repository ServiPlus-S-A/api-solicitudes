package com.traceability.solicitudes.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuditLogEntityTest {

    @Test
    void shouldCreateEntity() {

        AuditLogEntity entity =
                new AuditLogEntity(
                        "CREATE",
                        "user-1",
                        Map.of("id", "123"),
                        Instant.now());

        assertThat(entity).isNotNull();
    }
}