package com.traceability.solicitudes.application.service;

import com.traceability.solicitudes.application.command.CreateSolicitudCommand;
import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.exception.SolicitudNotFoundException;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.domain.service.SolicitudDomainService;
import com.traceability.solicitudes.infrastructure.cache.CacheNames;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SolicitudService {

    private final SolicitudRepositoryPort solicitudRepository;
    private final SolicitudDomainService domainService;
    private final CacheService cacheService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public SolicitudService(
            SolicitudRepositoryPort solicitudRepository,
            SolicitudDomainService domainService,
            CacheService cacheService,
            AuditService auditService,
            NotificationService notificationService) {
        this.solicitudRepository = solicitudRepository;
        this.domainService = domainService;
        this.cacheService = cacheService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    public Solicitud create(CreateSolicitudCommand command, String actorUserId) {
        domainService.validateForCreate(command.titulo(), command.descripcion(), command.solicitanteId());

        Instant now = Instant.now();
        Solicitud solicitud = new Solicitud(
                UUID.randomUUID(),
                command.titulo(),
                command.descripcion(),
                command.solicitanteId(),
                SolicitudEstado.ENVIADA,
                now,
                now);

        Solicitud saved = solicitudRepository.save(solicitud);
        cacheService.evictMetricas();
        auditService.logCreate(actorUserId, saved.getId().toString(), saved.getTitulo());
        notificationService.notifySolicitudCreated(
                command.solicitanteId(), saved.getId().toString(), saved.getTitulo());
        return saved;
    }

    @Cacheable(value = CacheNames.SOLICITUD, key = "#id")
    public Solicitud getById(UUID id) {
        return solicitudRepository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));
    }

    public List<Solicitud> list(int page, int size) {
        return solicitudRepository.findAll(page, size);
    }

    public Solicitud updateEstado(UUID id, SolicitudEstado nuevoEstado, String actorUserId) {
        Solicitud existing = solicitudRepository.findById(id).orElseThrow(() -> new SolicitudNotFoundException(id));
        domainService.validateStateTransition(existing, nuevoEstado);

        SolicitudEstado anterior = existing.getEstado();
        Solicitud updated = existing.withEstado(nuevoEstado, Instant.now());
        Solicitud saved = solicitudRepository.save(updated);

        cacheService.evictSolicitud(id);
        cacheService.evictMetricas();
        auditService.logStateChange(actorUserId, id.toString(), anterior.name(), nuevoEstado.name());
        return saved;
    }
}
