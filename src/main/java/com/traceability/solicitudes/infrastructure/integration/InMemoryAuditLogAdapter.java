package com.traceability.solicitudes.infrastructure.integration;

import com.traceability.solicitudes.application.port.AuditLogPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("inmemory")
public class InMemoryAuditLogAdapter implements AuditLogPort {

    private final List<AuditEntry> entries = new ArrayList<>();

    @Override
    public void record(String action, String userId, Map<String, String> metadata) {
        entries.add(new AuditEntry(action, userId, Map.copyOf(metadata)));
    }

    public List<AuditEntry> getEntries() {
        return List.copyOf(entries);
    }

    public record AuditEntry(String action, String userId, Map<String, String> metadata) {
    }
}
