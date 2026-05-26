package com.traceability.solicitudes.infrastructure.persistence.jpa.entity;

import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "solicitudes")
public class SolicitudEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "solicitante_id", nullable = false)
    private String solicitanteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SolicitudEstado estado;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected SolicitudEntity() {
    }

    public SolicitudEntity(
            UUID id,
            String titulo,
            String descripcion,
            String solicitanteId,
            SolicitudEstado estado,
            Instant creadoEn,
            Instant actualizadoEn) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solicitanteId = solicitanteId;
        this.estado = estado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public static SolicitudEntity fromDomain(Solicitud solicitud) {
        return new SolicitudEntity(
                solicitud.getId(),
                solicitud.getTitulo(),
                solicitud.getDescripcion(),
                solicitud.getSolicitanteId(),
                solicitud.getEstado(),
                solicitud.getCreadoEn(),
                solicitud.getActualizadoEn());
    }

    public Solicitud toDomain() {
        return new Solicitud(id, titulo, descripcion, solicitanteId, estado, creadoEn, actualizadoEn);
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getSolicitanteId() {
        return solicitanteId;
    }

    public SolicitudEstado getEstado() {
        return estado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
