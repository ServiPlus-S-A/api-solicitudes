package com.traceability.solicitudes.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ArchivoAdjunto {

    private final UUID id;
    private final UUID solicitudId;
    private final String nombreArchivo;
    private final String contentType;
    private final String storageKey;
    private final Instant subidoEn;

    public ArchivoAdjunto(
            UUID id,
            UUID solicitudId,
            String nombreArchivo,
            String contentType,
            String storageKey,
            Instant subidoEn) {
        this.id = id;
        this.solicitudId = solicitudId;
        this.nombreArchivo = nombreArchivo;
        this.contentType = contentType;
        this.storageKey = storageKey;
        this.subidoEn = subidoEn;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getContentType() {
        return contentType;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Instant getSubidoEn() {
        return subidoEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArchivoAdjunto that = (ArchivoAdjunto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
