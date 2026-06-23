package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.BaseIntegrationTest;
import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.model.SolicitudModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AdjuntoRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AdjuntoRepository adjuntoRepository;

    @Autowired
    private SolicitudRepository solicitudRepository; // 👈 Inyectamos para cumplir la FK

    @Test
    void givenAdjunto_whenFindByIdSolicitud_thenReturnList() {
        // Arrange - Primero creamos y guardamos la solicitud padre requerida por la FK
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(1L)
                .asunto("Soporte Técnico")
                .descripcion("Error de conexión")
                .ubicacion("Cali")
                .codigoTrazabilidad("TR-ADJ-01")
                .build();
        SolicitudModel solicitudGuardada = solicitudRepository.save(solicitud);

        // Ahora asociamos el adjunto al ID real generado por la base de datos
        AdjuntoModel adjunto = AdjuntoModel.builder()
                .idSolicitud(solicitudGuardada.getId()) // 👈 Usamos el ID real de la FK
                .urlArchivo("https://s3.amazonaws.com/archivo.pdf")
                .build();
        adjuntoRepository.save(adjunto);

        // Act
        List<AdjuntoModel> result = adjuntoRepository.findByIdSolicitud(solicitudGuardada.getId());

        // Assert
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("https://s3.amazonaws.com/archivo.pdf", result.get(0).getUrlArchivo());
    }
}