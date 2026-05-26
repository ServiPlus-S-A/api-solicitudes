package com.traceability.solicitudes.application.port;

import java.util.Map;

public interface AuditLogPort {

    void record(String action, String userId, Map<String, String> metadata);
}
