package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AsignacionModel;
import com.traceability.solicitudes.repository.AsignacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AsignacionServiceTest {

    private static final LocalDateTime FECHA_TEST = LocalDateTime.of(2026, Month.JUNE, 23, 12, 0);

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
        // Arrange
        AsignacionModel asignacion = AsignacionModel.builder()
                .idSolicitud(1L)
                .idConsultor(99L)
                .fechaAsignacion(FECHA_TEST)
                .build();

        when(asignacionRepository.save(any(AsignacionModel.class))).thenReturn(asignacion);

        // Act
        AsignacionModel resultado = asignacionService.guardarAsignacion(asignacion);

        // Assert
        assertNotNull(resultado);
        assertEquals(99L, resultado.getIdConsultor());
        verify(asignacionRepository, times(1)).save(asignacion);
    }

    @Test
    void cuandoObtenerPorSolicitud_entoncesRetornaListaDeAsignaciones() {
        // Arrange
        Long idSolicitud = 1L;
        AsignacionModel asignacion1 = AsignacionModel.builder()
                .id(101L)
                .idSolicitud(idSolicitud)
                .idConsultor(99L)
                .fechaAsignacion(FECHA_TEST)
                .build();

        AsignacionModel asignacion2 = AsignacionModel.builder()
                .id(102L)
                .idSolicitud(idSolicitud)
                .idConsultor(88L)
                .fechaAsignacion(FECHA_TEST)
                .build();

        List<AsignacionModel> listaEsperada = Arrays.asList(asignacion1, asignacion2);
        when(asignacionRepository.findByIdSolicitud(idSolicitud)).thenReturn(listaEsperada);

        // Act
        List<AsignacionModel> resultado = asignacionService.obtenerPorSolicitud(idSolicitud);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        assertEquals(99L, resultado.get(0).getIdConsultor());
        verify(asignacionRepository, times(1)).findByIdSolicitud(idSolicitud);
    }
}