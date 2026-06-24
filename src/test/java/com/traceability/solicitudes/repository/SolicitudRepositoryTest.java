package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.BaseIntegrationTest;
import com.traceability.solicitudes.model.EstadoSolicitud;
import com.traceability.solicitudes.model.SolicitudModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

class SolicitudRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Test
    void givenSolicitudesInDb_whenFindAllByIdCliente_thenReturnFilteredPage() {
        SolicitudModel s1 = SolicitudModel.builder()
                .idCliente(55L)
                .idTipoServicio(1L)
                .asunto("Fallo")
                .descripcion("Descripción de prueba para falla de servicio")
                .ubicacion("Cali")
                .estado(EstadoSolicitud.PENDIENTE)
                .codigoTrazabilidad("TR-01")
                .build();

        SolicitudModel s2 = SolicitudModel.builder()
                .idCliente(99L)
                .idTipoServicio(1L)
                .asunto("Fallo 2")
                .descripcion("Segunda descripción de prueba obligatoria")
                .ubicacion("Cali")
                .estado(EstadoSolicitud.PENDIENTE)
                .codigoTrazabilidad("TR-02")
                .build();

        solicitudRepository.save(s1);
        solicitudRepository.save(s2);

        // Act
        Page<SolicitudModel> result = solicitudRepository.findAllByIdCliente(55L, PageRequest.of(0, 10));

        // Assert
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("TR-01", result.getContent().get(0).getCodigoTrazabilidad());
    }

    @Test
    void givenCodigoTrazabilidad_whenFindByCodigoTrazabilidad_thenReturnSolicitud() {
        SolicitudModel s = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(1L)
                .asunto("Test")
                .descripcion("Descripción obligatoria para búsqueda por trazabilidad")
                .ubicacion("Cali")
                .estado(EstadoSolicitud.PENDIENTE)
                .codigoTrazabilidad("TR-FIND")
                .build();

        solicitudRepository.save(s);

        // Act
        Optional<SolicitudModel> result = solicitudRepository.findByCodigoTrazabilidad("TR-FIND");

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("TR-FIND", result.get().getCodigoTrazabilidad());
    }
}