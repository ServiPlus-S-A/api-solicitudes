package com.traceability.solicitudes.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class InMemoryAuditLogAdapterTest {

    @Test
    void shouldStoreAuditEntry() {

        InMemoryAuditLogAdapter adapter =
                new InMemoryAuditLogAdapter();

        adapter.record(
                "CREATE_SOLICITUD",
                "user-123",
                Map.of("solicitudId", "1"));

        var entries = adapter.getEntries();

        assertThat(entries).hasSize(1);

        var entry = entries.getFirst();

        assertThat(entry.action()).isEqualTo("CREATE_SOLICITUD");
        assertThat(entry.userId()).isEqualTo("user-123");
        assertThat(entry.metadata())
                .containsEntry("solicitudId", "1");
    }

    @Test
    void shouldReturnImmutableEntriesList() {

        InMemoryAuditLogAdapter adapter =
                new InMemoryAuditLogAdapter();

        adapter.record(
                "ACTION",
                "user",
                Map.of());

        var entries = adapter.getEntries();

        assertThat(entries)
                .hasSize(1);
    }
}