package com.traceability.solicitudes.application.service;

import com.traceability.solicitudes.application.port.FileRepositoryPort;
import com.traceability.solicitudes.application.port.FileStoragePort;
import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.exception.SolicitudNotFoundException;
import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import com.traceability.solicitudes.domain.model.Solicitud;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final SolicitudRepositoryPort solicitudRepository;
    private final FileRepositoryPort fileRepository;
    private final FileStoragePort fileStorage;

    public FileService(
            SolicitudRepositoryPort solicitudRepository,
            FileRepositoryPort fileRepository,
            FileStoragePort fileStorage) {
        this.solicitudRepository = solicitudRepository;
        this.fileRepository = fileRepository;
        this.fileStorage = fileStorage;
    }

    @CircuitBreaker(name = "externalStorage")
    public ArchivoAdjunto upload(UUID solicitudId, String nombreArchivo, String contentType, byte[] content) {
        Solicitud solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));

        String storageKey = "solicitudes/" + solicitud.getId() + "/" + UUID.randomUUID();
        String storedKey = fileStorage.store(storageKey, content, contentType);

        ArchivoAdjunto archivo = new ArchivoAdjunto(
                UUID.randomUUID(),
                solicitudId,
                nombreArchivo,
                contentType,
                storedKey,
                Instant.now());

        return fileRepository.save(archivo);
    }

    public List<ArchivoAdjunto> listBySolicitud(UUID solicitudId) {
        if (solicitudRepository.findById(solicitudId).isEmpty()) {
            throw new SolicitudNotFoundException(solicitudId);
        }
        return fileRepository.findBySolicitudId(solicitudId);
    }

    public byte[] download(UUID archivoId) {
        ArchivoAdjunto archivo = fileRepository
                .findById(archivoId)
                .orElseThrow(() -> new SolicitudNotFoundException(archivoId));
        return fileStorage.retrieve(archivo.getStorageKey());
    }
}
