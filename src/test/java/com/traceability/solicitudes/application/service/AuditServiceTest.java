package com.traceability.solicitudes.application.service;

import static org.mockito.Mockito.verify;

import com.traceability.solicitudes.application.port.AuditLogPort;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogPort auditLogPort;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditLogPort);
    }

    @Test
    void should_log_create() {
        auditService.logCreate("user-1", "sol-1", "Titulo");

        verify(auditLogPort).record("SOLICITUD_CREATED", "user-1", Map.of("solicitudId", "sol-1", "titulo", "Titulo"));
    }

    @Test
    void should_log_export() {
        auditService.logExport("user-1", "PDF", Map.of("estado", "APROBADA"));

        verify(auditLogPort)
                .record("REPORT_EXPORTED", "user-1", Map.of("formato", "PDF", "filtros", "{estado=APROBADA}"));
    }

    @Test
    void should_log_state_change() {
        auditService.logStateChange("user-1", "sol-1", "ENVIADA", "APROBADA");

        verify(auditLogPort)
                .record(
                        "SOLICITUD_STATE_CHANGED",
                        "user-1",
                        Map.of("solicitudId", "sol-1", "estadoAnterior", "ENVIADA", "estadoNuevo", "APROBADA"));
    }
}
