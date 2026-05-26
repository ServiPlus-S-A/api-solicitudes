package com.traceability.solicitudes.presentation.controller;

import com.traceability.solicitudes.application.service.FileService;
import com.traceability.solicitudes.presentation.dto.file.ArchivoResponse;
import com.traceability.solicitudes.presentation.mapper.FileDtoMapper;
import com.traceability.solicitudes.utils.Constants;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/files")
public class FileController {

    private final FileService fileService;
    private final FileDtoMapper mapper;

    public FileController(FileService fileService, FileDtoMapper mapper) {
        this.fileService = fileService;
        this.mapper = mapper;
    }

    @PostMapping(value = "/solicitudes/{solicitudId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE', 'SOLICITANTE')")
    public ArchivoResponse upload(
            @PathVariable UUID solicitudId,
            @RequestParam("file") MultipartFile file)
            throws Exception {
        var archivo = fileService.upload(
                solicitudId,
                file.getOriginalFilename(),
                file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE,
                file.getBytes());
        return mapper.toResponse(archivo);
    }

    @GetMapping("/solicitudes/{solicitudId}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE', 'SOLICITANTE')")
    public List<ArchivoResponse> list(@PathVariable UUID solicitudId) {
        return fileService.listBySolicitud(solicitudId).stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{archivoId}/download")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE')")
    public ResponseEntity<byte[]> download(@PathVariable UUID archivoId) {
        byte[] content = fileService.download(archivoId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
