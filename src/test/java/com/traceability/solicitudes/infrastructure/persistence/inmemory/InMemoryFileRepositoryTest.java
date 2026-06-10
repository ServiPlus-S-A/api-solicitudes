package com.traceability.solicitudes.infrastructure.persistence.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryFileRepositoryTest {

    private final InMemoryFileRepository repository = new InMemoryFileRepository();

    @Test
    void shouldSaveAndFindById() {

        UUID id = UUID.randomUUID();

        ArchivoAdjunto archivo =
                new ArchivoAdjunto(
                        id,
                        UUID.randomUUID(),
                        "archivo.pdf",
                        "application/pdf",
                        "storage-key",
                        Instant.now());

        repository.save(archivo);

        assertThat(repository.findById(id))
                .isPresent()
                .contains(archivo);
    }

    @Test
    void shouldReturnEmptyWhenFileDoesNotExist() {

        assertThat(repository.findById(UUID.randomUUID()))
                .isEmpty();
    }

    @Test
    void shouldFindFilesBySolicitudId() {

        UUID solicitudId = UUID.randomUUID();

        ArchivoAdjunto archivo1 =
                new ArchivoAdjunto(
                        UUID.randomUUID(),
                        solicitudId,
                        "a.pdf",
                        "application/pdf",
                        "k1",
                        Instant.now());

        ArchivoAdjunto archivo2 =
                new ArchivoAdjunto(
                        UUID.randomUUID(),
                        solicitudId,
                        "b.pdf",
                        "application/pdf",
                        "k2",
                        Instant.now());

        ArchivoAdjunto otroArchivo =
                new ArchivoAdjunto(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "c.pdf",
                        "application/pdf",
                        "k3",
                        Instant.now());

        repository.save(archivo1);
        repository.save(archivo2);
        repository.save(otroArchivo);

        assertThat(repository.findBySolicitudId(solicitudId))
                .containsExactlyInAnyOrder(archivo1, archivo2);
    }
}