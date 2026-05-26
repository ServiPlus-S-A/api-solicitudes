package com.traceability.solicitudes.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.traceability.solicitudes.application.port.FileRepositoryPort;
import com.traceability.solicitudes.application.port.FileStoragePort;
import com.traceability.solicitudes.application.port.SolicitudRepositoryPort;
import com.traceability.solicitudes.domain.exception.SolicitudNotFoundException;
import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
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
class FileServiceTest {

    @Mock
    private SolicitudRepositoryPort solicitudRepository;

    @Mock
    private FileRepositoryPort fileRepository;

    @Mock
    private FileStoragePort fileStorage;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(solicitudRepository, fileRepository, fileStorage);
    }

    @Test
    void should_upload_file_when_solicitud_exists() {
        UUID solicitudId = UUID.randomUUID();
        Solicitud solicitud =
                new Solicitud(solicitudId, "T", "D", "u1", SolicitudEstado.ENVIADA, Instant.now(), Instant.now());
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(fileStorage.store(any(), any(), any())).thenReturn("key-1");
        when(fileRepository.save(any(ArchivoAdjunto.class))).thenAnswer(inv -> inv.getArgument(0));

        ArchivoAdjunto result = fileService.upload(solicitudId, "doc.pdf", "application/pdf", new byte[] {1, 2});

        assertThat(result.getNombreArchivo()).isEqualTo("doc.pdf");
        assertThat(result.getStorageKey()).isEqualTo("key-1");
    }

    @Test
    void should_fail_upload_when_solicitud_missing() {
        UUID solicitudId = UUID.randomUUID();
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.upload(solicitudId, "doc.pdf", "application/pdf", new byte[] {1}))
                .isInstanceOf(SolicitudNotFoundException.class);
    }

    @Test
    void should_list_files_by_solicitud() {
        UUID solicitudId = UUID.randomUUID();
        when(solicitudRepository.findById(solicitudId))
                .thenReturn(Optional.of(new Solicitud(
                        solicitudId, "T", "D", "u1", SolicitudEstado.ENVIADA, Instant.now(), Instant.now())));
        when(fileRepository.findBySolicitudId(solicitudId)).thenReturn(List.of());

        assertThat(fileService.listBySolicitud(solicitudId)).isEmpty();
    }

    @Test
    void should_throw_when_listing_files_for_missing_solicitud() {
        UUID solicitudId = UUID.randomUUID();
        when(solicitudRepository.findById(solicitudId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.listBySolicitud(solicitudId))
                .isInstanceOf(SolicitudNotFoundException.class);
    }

    @Test
    void should_throw_when_downloading_missing_file() {
        UUID archivoId = UUID.randomUUID();
        when(fileRepository.findById(archivoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.download(archivoId)).isInstanceOf(SolicitudNotFoundException.class);
    }

    @Test
    void should_download_file() {
        UUID archivoId = UUID.randomUUID();
        ArchivoAdjunto archivo = new ArchivoAdjunto(
                archivoId, UUID.randomUUID(), "a.pdf", "application/pdf", "key-1", Instant.now());
        when(fileRepository.findById(archivoId)).thenReturn(Optional.of(archivo));
        when(fileStorage.retrieve("key-1")).thenReturn(new byte[] {9});

        assertThat(fileService.download(archivoId)).containsExactly(9);
    }
}
