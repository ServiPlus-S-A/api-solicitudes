package com.traceability.solicitudes.infrastructure.persistence.jpa.entity;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "archivos_adjuntos")
public class ArchivoAdjuntoEntity {

    @Id
    private UUID id;

    @Column(name = "solicitud_id", nullable = false)
    private UUID solicitudId;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "subido_en", nullable = false)
    private Instant subidoEn;

    protected ArchivoAdjuntoEntity() {
    }

    public ArchivoAdjuntoEntity(
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

    public static ArchivoAdjuntoEntity fromDomain(ArchivoAdjunto archivo) {
        return new ArchivoAdjuntoEntity(
                archivo.getId(),
                archivo.getSolicitudId(),
                archivo.getNombreArchivo(),
                archivo.getContentType(),
                archivo.getStorageKey(),
                archivo.getSubidoEn());
    }

    public ArchivoAdjunto toDomain() {
        return new ArchivoAdjunto(id, solicitudId, nombreArchivo, contentType, storageKey, subidoEn);
    }
}
