package com.traceability.solicitudes.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.traceability.solicitudes.application.command.CreateSolicitudCommand;
import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.exception.SolicitudNotFoundException;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.domain.service.SolicitudDomainService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepositoryPort repository;

    @Mock
    private CacheService cacheService;

    @Mock
    private AuditService auditService;

    @Mock
    private NotificationService notificationService;

    private SolicitudService solicitudService;

    @BeforeEach
    void setUp() {
        solicitudService = new SolicitudService(
                repository, new SolicitudDomainService(), cacheService, auditService, notificationService);
    }

    @Test
    void should_create_solicitud_and_trigger_side_effects() {
        // Arrange
        var command = new CreateSolicitudCommand("Titulo", "Descripcion", "user-1");
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Solicitud result = solicitudService.create(command, "actor-1");

        // Assert
        assertThat(result.getTitulo()).isEqualTo("Titulo");
        assertThat(result.getEstado()).isEqualTo(SolicitudEstado.ENVIADA);
        verify(cacheService).evictMetricas();
        verify(auditService).logCreate(eq("actor-1"), any(), eq("Titulo"));
        verify(notificationService).notifySolicitudCreated(eq("user-1"), any(), eq("Titulo"));
    }

    @Test
    void should_get_by_id_when_exists() {
        UUID id = UUID.randomUUID();
        Solicitud solicitud =
                new Solicitud(id, "T", "D", "u1", SolicitudEstado.ENVIADA, Instant.now(), Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(solicitud));

        assertThat(solicitudService.getById(id).getId()).isEqualTo(id);
    }

    @Test
    void should_throw_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitudService.getById(id)).isInstanceOf(SolicitudNotFoundException.class);
    }

    @Test
    void should_list_solicitudes() {
        when(repository.findAll(0, 10)).thenReturn(List.of());

        assertThat(solicitudService.list(0, 10)).isEmpty();
    }

    @Test
    void should_throw_when_update_estado_and_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> solicitudService.updateEstado(id, SolicitudEstado.APROBADA, "actor"))
                .isInstanceOf(SolicitudNotFoundException.class);
    }

    @Test
    void should_update_estado_and_invalidate_cache() {
        UUID id = UUID.randomUUID();
        Solicitud existing =
                new Solicitud(id, "T", "D", "u1", SolicitudEstado.ENVIADA, Instant.now(), Instant.now());
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Solicitud updated = solicitudService.updateEstado(id, SolicitudEstado.EN_REVISION, "actor-2");

        assertThat(updated.getEstado()).isEqualTo(SolicitudEstado.EN_REVISION);
        verify(cacheService).evictSolicitud(id);
        verify(cacheService).evictMetricas();
        verify(auditService).logStateChange("actor-2", id.toString(), "ENVIADA", "EN_REVISION");
    }
}
