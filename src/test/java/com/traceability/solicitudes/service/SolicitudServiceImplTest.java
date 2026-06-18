package com.traceability.solicitudes.service;

import com.traceability.solicitudes.exception.BusinessException;
import com.traceability.solicitudes.exception.ResourceNotFoundException;
import com.traceability.solicitudes.integration.ClienteClient;
import com.traceability.solicitudes.integration.ServicioClient;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.repository.SolicitudRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.Optional;

/**
 * Pruebas unitarias para SolicitudServiceImpl utilizando JUnit 5 y Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudServiceImplTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ServicioClient servicioClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MetricService metricService;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    @Test
    void givenValidSolicitud_whenCrear_thenReturnCreada() {
        // Given
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .asunto("Soporte de Conexión")
                .descripcion("Problemas con la red principal")
                .urlAdjunto("http://bucket.supabase.com/file123.jpg")
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(servicioClient.obtenerServicio(2L)).thenReturn("Servicio 2 info");
        Mockito.when(solicitudRepository.findByCodigoTrazabilidad(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenAnswer(invocation -> {
            SolicitudModel model = invocation.getArgument(0);
            model.setId(100L);
            return model;
        });

        // When
        SolicitudModel resultado = solicitudService.crear(solicitud);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(100L, resultado.getId());
        Assertions.assertNotNull(resultado.getCodigoTrazabilidad());
        Assertions.assertTrue(resultado.getCodigoTrazabilidad().startsWith("TR-"));
        Assertions.assertEquals("http://bucket.supabase.com/file123.jpg", resultado.getUrlAdjunto());

        Mockito.verify(clienteClient, Mockito.times(1)).obtenerCliente(1L);
        Mockito.verify(servicioClient, Mockito.times(1)).obtenerServicio(2L);
        Mockito.verify(solicitudRepository, Mockito.times(1)).save(Mockito.any(SolicitudModel.class));
    }

    @Test
    void givenSolicitudWithDuplicateCode_whenCrear_thenThrowBusinessException() {
        // Given
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .codigoTrazabilidad("TR-DUP")
                .asunto("Soporte")
                .descripcion("Detalle")
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(servicioClient.obtenerServicio(2L)).thenReturn("Servicio 2 info");
        Mockito.when(solicitudRepository.findByCodigoTrazabilidad("TR-DUP")).thenReturn(Optional.of(solicitud));

        // When & Then
        Assertions.assertThrows(BusinessException.class, () -> solicitudService.crear(solicitud));
        Mockito.verify(solicitudRepository, Mockito.never()).save(Mockito.any(SolicitudModel.class));
    }

    @Test
    void givenExistingSolicitud_whenActualizar_thenReturnActualizada() {
        // Given
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(100L)
                .idCliente(1L)
                .idTipoServicio(2L)
                .asunto("Soporte Actualizado")
                .descripcion("Detalle")
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenReturn(solicitud);

        // When
        SolicitudModel resultado = solicitudService.actualizar(solicitud);

        // Then
        Assertions.assertNotNull(resultado);
        Mockito.verify(solicitudRepository, Mockito.times(1)).save(solicitud);
    }

    @Test
    void givenNonExistingSolicitud_whenActualizar_thenThrowResourceNotFoundException() {
        // Given
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(100L)
                .idCliente(1L)
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1");
        Mockito.when(solicitudRepository.findById(100L)).thenReturn(Optional.empty());

        // When & Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> solicitudService.actualizar(solicitud));
        Mockito.verify(solicitudRepository, Mockito.never()).save(Mockito.any(SolicitudModel.class));
    }

    @Test
    void givenExistingId_whenEliminar_thenExecuteDelete() {
        // Given
        Mockito.when(solicitudRepository.existsById(100L)).thenReturn(true);

        // When
        solicitudService.eliminar(100L);

        // Then
        Mockito.verify(solicitudRepository, Mockito.times(1)).deleteById(100L);
    }

    @Test
    void givenNonExistingId_whenEliminar_thenThrowResourceNotFoundException() {
        // Given
        Mockito.when(solicitudRepository.existsById(100L)).thenReturn(false);

        // When & Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> solicitudService.eliminar(100L));
        Mockito.verify(solicitudRepository, Mockito.never()).deleteById(100L);
    }

    @Test
    void givenPageable_whenObtenerTodos_thenReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<SolicitudModel> pagina = new PageImpl<>(Collections.emptyList());
        Mockito.when(solicitudRepository.findAll(pageable)).thenReturn(pagina);

        // When
        Page<SolicitudModel> resultado = solicitudService.obtenerTodos(pageable);

        // Then
        Assertions.assertNotNull(resultado);
        Mockito.verify(solicitudRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void givenExistingId_whenObtenerPorId_thenReturnSolicitud() {
        // Given
        SolicitudModel solicitud = SolicitudModel.builder().id(100L).build();
        Mockito.when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));

        // When
        SolicitudModel resultado = solicitudService.obtenerPorId(100L);

        // Then
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(100L, resultado.getId());
    }

    @Test
    void givenNonExistingId_whenObtenerPorId_thenThrowResourceNotFoundException() {
        // Given
        Mockito.when(solicitudRepository.findById(100L)).thenReturn(Optional.empty());

        // When & Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> solicitudService.obtenerPorId(100L));
    }

    @Test
    void givenClienteIdAndPageable_whenBuscarPorCliente_thenReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<SolicitudModel> pagina = new PageImpl<>(Collections.emptyList());
        Mockito.when(solicitudRepository.findAllByIdCliente(1L, pageable)).thenReturn(pagina);

        // When
        Page<SolicitudModel> resultado = solicitudService.buscarPorCliente(1L, pageable);

        // Then
        Assertions.assertNotNull(resultado);
        Mockito.verify(solicitudRepository, Mockito.times(1)).findAllByIdCliente(1L, pageable);
    }

    @Test
    void givenEstadoAndPageable_whenBuscarPorEstado_thenReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<SolicitudModel> pagina = new PageImpl<>(Collections.emptyList());
        Mockito.when(solicitudRepository.findAllByEstado("Pendiente", pageable)).thenReturn(pagina);

        // When
        Page<SolicitudModel> resultado = solicitudService.buscarPorEstado("Pendiente", pageable);

        // Then
        Assertions.assertNotNull(resultado);
        Mockito.verify(solicitudRepository, Mockito.times(1)).findAllByEstado("Pendiente", pageable);
    }
}
