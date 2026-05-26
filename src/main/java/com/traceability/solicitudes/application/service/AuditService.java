package com.traceability.solicitudes.application.service;

import com.traceability.solicitudes.application.port.AuditLogPort;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogPort auditLogPort;

    public AuditService(AuditLogPort auditLogPort) {
        this.auditLogPort = auditLogPort;
    }

    public void logCreate(String userId, String solicitudId, String titulo) {
        auditLogPort.record(
                "SOLICITUD_CREATED",
                userId,
                Map.of("solicitudId", solicitudId, "titulo", titulo));
    }

    public void logExport(String userId, String formato, Map<String, String> filtros) {
        auditLogPort.record(
                "REPORT_EXPORTED",
                userId,
                Map.of("formato", formato, "filtros", filtros.toString()));
    }

    public void logStateChange(String userId, String solicitudId, String estadoAnterior, String estadoNuevo) {
        auditLogPort.record(
                "SOLICITUD_STATE_CHANGED",
                userId,
                Map.of(
                        "solicitudId", solicitudId,
                        "estadoAnterior", estadoAnterior,
                        "estadoNuevo", estadoNuevo));
    }
}
