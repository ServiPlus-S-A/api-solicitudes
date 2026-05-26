package com.traceability.solicitudes.presentation.mapper;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import com.traceability.solicitudes.presentation.dto.file.ArchivoResponse;
import org.springframework.stereotype.Component;

@Component
public class FileDtoMapper {

    public ArchivoResponse toResponse(ArchivoAdjunto archivo) {
        return new ArchivoResponse(
                archivo.getId(),
                archivo.getSolicitudId(),
                archivo.getNombreArchivo(),
                archivo.getContentType(),
                archivo.getSubidoEn());
    }
}
