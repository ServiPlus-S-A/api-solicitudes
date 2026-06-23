package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.repository.AdjuntoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdjuntoServiceTest {

    @Mock
    private AdjuntoRepository adjuntoRepository;

    @InjectMocks
    private AdjuntoService adjuntoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cuandoGuardarAdjunto_entoncesRetornaAdjuntoGuardado() {
        // Arrange
        AdjuntoModel adjunto = AdjuntoModel.builder()
                .idSolicitud(1L)
                .urlArchivo("https://supabase.com/storage/evidencia.pdf")
                .tipoArchivo("PDF")
                .build();

        when(adjuntoRepository.save(any(AdjuntoModel.class))).thenReturn(adjunto);

        // Act
        AdjuntoModel resultado = adjuntoService.guardarAdjunto(adjunto);

        // Assert
        assertNotNull(resultado);
        assertEquals("PDF", resultado.getTipoArchivo());
        verify(adjuntoRepository, times(1)).save(adjunto);
    }

    @Test
    void cuandoObtenerPorSolicitud_entoncesRetornaListaDeAdjuntos() {
        // Arrange
        AdjuntoModel adjunto1 = AdjuntoModel.builder().idSolicitud(1L).urlArchivo("url1").tipoArchivo("JPG").build();
        AdjuntoModel adjunto2 = AdjuntoModel.builder().idSolicitud(1L).urlArchivo("url2").tipoArchivo("PNG").build();

        when(adjuntoRepository.findByIdSolicitud(1L)).thenReturn(Arrays.asList(adjunto1, adjunto2));

        // Act
        List<AdjuntoModel> resultado = adjuntoService.obtenerPorSolicitud(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(adjuntoRepository, times(1)).findByIdSolicitud(1L);
    }
}