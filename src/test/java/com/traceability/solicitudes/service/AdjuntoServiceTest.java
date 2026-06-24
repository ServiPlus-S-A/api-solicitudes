package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.AdjuntoModel;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.repository.AdjuntoRepository;
import com.traceability.solicitudes.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdjuntoServiceTest {

    @Mock
    private AdjuntoRepository adjuntoRepository;

    @Mock
    private SolicitudRepository solicitudRepository; // ← AGREGADO

    @InjectMocks
    private AdjuntoService adjuntoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cuandoGuardarAdjunto_entoncesRetornaAdjuntoGuardado() {
        // Arrange
        SolicitudModel mockSolicitud = SolicitudModel.builder().id(1L).build();

        AdjuntoModel adjunto = AdjuntoModel.builder()
                .solicitud(mockSolicitud)
                .urlArchivo("https://supabase.com/storage/evidencia.pdf")
                .tipoArchivo("PDF")
                .build();

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(mockSolicitud)); // ← AGREGADO
        when(adjuntoRepository.save(any(AdjuntoModel.class))).thenReturn(adjunto);

        // Act
        AdjuntoModel resultado = adjuntoService.guardarAdjunto(adjunto, 1L); // ← AGREGADO el segundo argumento

        // Assert
        assertNotNull(resultado);
        assertEquals("PDF", resultado.getTipoArchivo());
        verify(adjuntoRepository, times(1)).save(adjunto);
    }

    @Test
    void cuandoObtenerPorSolicitud_entoncesRetornaListaDeAdjuntos() {
        // Arrange
        SolicitudModel mockSolicitud = SolicitudModel.builder().id(1L).build();

        AdjuntoModel adjunto1 = AdjuntoModel.builder().solicitud(mockSolicitud).urlArchivo("url1").tipoArchivo("JPG").build();
        AdjuntoModel adjunto2 = AdjuntoModel.builder().solicitud(mockSolicitud).urlArchivo("url2").tipoArchivo("PNG").build();

        when(adjuntoRepository.findBySolicitudId(1L)).thenReturn(Arrays.asList(adjunto1, adjunto2));

        // Act
        List<AdjuntoModel> resultado = adjuntoService.obtenerPorSolicitud(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(adjuntoRepository, times(1)).findBySolicitudId(1L);
    }
}