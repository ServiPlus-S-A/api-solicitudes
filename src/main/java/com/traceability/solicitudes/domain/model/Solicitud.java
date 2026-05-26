package com.traceability.solicitudes.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Solicitud {

    private final UUID id;
    private final String titulo;
    private final String descripcion;
    private final String solicitanteId;
    private final SolicitudEstado estado;
    private final Instant creadoEn;
    private final Instant actualizadoEn;

    public Solicitud(
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

    public Solicitud withEstado(SolicitudEstado nuevoEstado, Instant actualizadoEn) {
        return new Solicitud(id, titulo, descripcion, solicitanteId, nuevoEstado, creadoEn, actualizadoEn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Solicitud solicitud = (Solicitud) o;
        return Objects.equals(id, solicitud.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
