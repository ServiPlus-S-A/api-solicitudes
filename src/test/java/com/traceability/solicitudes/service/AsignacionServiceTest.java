package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.repository.AsignacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AsignacionServiceTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cuandoGuardarAsignacion_entoncesRetornaAsignacionGuardada() {
        // Arrange (Preparar los datos)
        AsignacionModel asignacion = AsignacionModel.builder()
                .idSolicitud(1L)
                .idConsultor(99L)
                .fechaAsignacion(LocalDateTime.now())
                .build();

        when(asignacionRepository.save(any(AsignacionModel.class))).thenReturn(asignacion);

        // Act (Ejecutar la acción)
        AsignacionModel resultado = asignacionService.guardarAsignacion(asignacion);

        // Assert (Verificar que todo esté bien)
        assertNotNull(resultado);
        assertEquals(99L, resultado.getIdConsultor());
        verify(asignacionRepository, times(1)).save(asignacion);
    }
}