package com.traceability.solicitudes.infrastructure.persistence.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemorySolicitudRepositoryTest {

    private final InMemorySolicitudRepository repository =
            new InMemorySolicitudRepository();

    @Test
    void shouldSaveAndFindById() {

        Solicitud solicitud =
                new Solicitud(
                        UUID.randomUUID(),
                        "Titulo",
                        "Descripcion",
                        "usuario",
                        SolicitudEstado.ENVIADA,
                        Instant.now(),
                        Instant.now());

        repository.save(solicitud);

        assertThat(repository.findById(solicitud.getId()))
                .isPresent()
                .contains(solicitud);
    }

    @Test
    void shouldCountSolicitudes() {

        repository.save(
                new Solicitud(
                        UUID.randomUUID(),
                        "A",
                        "A",
                        "u1",
                        SolicitudEstado.ENVIADA,
                        Instant.now(),
                        Instant.now()));

        repository.save(
                new Solicitud(
                        UUID.randomUUID(),
                        "B",
                        "B",
                        "u2",
                        SolicitudEstado.APROBADA,
                        Instant.now(),
                        Instant.now()));

        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void shouldCountByEstado() {

        repository.save(
                new Solicitud(
                        UUID.randomUUID(),
                        "A",
                        "A",
                        "u1",
                        SolicitudEstado.APROBADA,
                        Instant.now(),
                        Instant.now()));

        repository.save(
                new Solicitud(
                        UUID.randomUUID(),
                        "B",
                        "B",
                        "u2",
                        SolicitudEstado.APROBADA,
                        Instant.now(),
                        Instant.now()));

        repository.save(
                new Solicitud(
                        UUID.randomUUID(),
                        "C",
                        "C",
                        "u3",
                        SolicitudEstado.RECHAZADA,
                        Instant.now(),
                        Instant.now()));

        assertThat(repository.countByEstado(SolicitudEstado.APROBADA))
                .isEqualTo(2);

        assertThat(repository.countByEstado(SolicitudEstado.RECHAZADA))
                .isEqualTo(1);
    }

    @Test
    void shouldReturnPagedResultsOrderedByDateDesc() {

        Instant now = Instant.now();

        Solicitud antigua =
                new Solicitud(
                        UUID.randomUUID(),
                        "Vieja",
                        "Desc",
                        "u1",
                        SolicitudEstado.ENVIADA,
                        now.minusSeconds(100),
                        now);

        Solicitud nueva =
                new Solicitud(
                        UUID.randomUUID(),
                        "Nueva",
                        "Desc",
                        "u2",
                        SolicitudEstado.ENVIADA,
                        now,
                        now);

        repository.save(antigua);
        repository.save(nueva);

        assertThat(repository.findAll(0, 10))
                .containsExactly(nueva, antigua);
    }
}