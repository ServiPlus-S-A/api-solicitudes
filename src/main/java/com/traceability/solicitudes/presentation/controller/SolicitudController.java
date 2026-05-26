package com.traceability.solicitudes.presentation.controller;

import com.traceability.solicitudes.application.service.SolicitudService;
import com.traceability.solicitudes.presentation.dto.solicitud.CreateSolicitudRequest;
import com.traceability.solicitudes.presentation.dto.solicitud.SolicitudResponse;
import com.traceability.solicitudes.presentation.dto.solicitud.UpdateEstadoRequest;
import com.traceability.solicitudes.presentation.mapper.SolicitudDtoMapper;
import com.traceability.solicitudes.utils.Constants;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.API_BASE_PATH + "/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final SolicitudDtoMapper mapper;

    public SolicitudController(SolicitudService solicitudService, SolicitudDtoMapper mapper) {
        this.solicitudService = solicitudService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE', 'SOLICITANTE')")
    public SolicitudResponse create(
            @Valid @RequestBody CreateSolicitudRequest request, @AuthenticationPrincipal Jwt jwt) {
        var created = solicitudService.create(mapper.toCommand(request), resolveUserId(jwt));
        return mapper.toResponse(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE', 'SOLICITANTE')")
    public SolicitudResponse getById(@PathVariable UUID id) {
        return mapper.toResponse(solicitudService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE')")
    public List<SolicitudResponse> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return solicitudService.list(page, size).stream().map(mapper::toResponse).toList();
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'GERENTE')")
    public SolicitudResponse updateEstado(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEstadoRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return mapper.toResponse(
                solicitudService.updateEstado(id, request.estado(), resolveUserId(jwt)));
    }

    private static String resolveUserId(Jwt jwt) {
        if (jwt == null) {
            return "system";
        }
        String sub = jwt.getSubject();
        return sub != null ? sub : "system";
    }
}
