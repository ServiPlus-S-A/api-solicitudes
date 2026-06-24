package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.BaseIntegrationTest;
import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.model.SolicitudModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

class AsignacionRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private SolicitudRepository solicitudRepository; // 👈 Inyectamos para cumplir la FK

    @Test
    void givenAsignacion_whenFindByIdSolicitud_thenReturnList() {
        // Arrange - Creamos y guardamos la solicitud padre
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(2L)
                .idTipoServicio(1L)
                .asunto("Mantenimiento")
                .descripcion("Revisión de servidores")
                .ubicacion("Cali")
                .codigoTrazabilidad("TR-ASIG-01")
                .build();
        SolicitudModel solicitudGuardada = solicitudRepository.save(solicitud);

        // Asociamos la asignación al ID correcto
        AsignacionModel asignacion = AsignacionModel.builder()
                .idSolicitud(solicitudGuardada.getId()) // 👈 Usamos el ID real de la FK
                .idConsultor(10L)
                .build();
        asignacionRepository.save(asignacion);

        // Act
        List<AsignacionModel> result = asignacionRepository.findByIdSolicitud(solicitudGuardada.getId());

        // Assert
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(solicitudGuardada.getId(), result.get(0).getIdSolicitud());
    }
}