package com.traceability.solicitudes.infrastructure.persistence.jpa;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.traceability.solicitudes.infrastructure.persistence.jpa.repository.AuditLogJpaRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaAuditLogAdapterTest {

    @Mock
    private AuditLogJpaRepository repository;

    @InjectMocks
    private JpaAuditLogAdapter adapter;

    @Test
    void shouldRecordAuditLog() {

        adapter.record(
                "CREATE",
                "user-1",
                Map.of("id", "123"));

        verify(repository).save(any());
    }
}