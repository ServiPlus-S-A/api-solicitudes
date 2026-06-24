package com.traceability.solicitudes.service;

import com.traceability.solicitudes.exception.BusinessException;
import com.traceability.solicitudes.exception.ResourceNotFoundException;
import com.traceability.solicitudes.integration.ClienteClient;
import com.traceability.solicitudes.integration.ServicioClient;
import com.traceability.solicitudes.model.SolicitudEstados;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.repository.AsignacionRepository;
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

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ServicioClient servicioClient;

    @Mock
    private NotificationService notificationService;

    @Mock
    private MetricService metricService;

    @InjectMocks
    private SolicitudService solicitudService;

    @Test
    void givenValidSolicitud_whenCrear_thenReturnCreada() {
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .asunto("Soporte de Conexión")
                .descripcion("Problemas con la red principal")
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(servicioClient.obtenerServicio(2L)).thenReturn("Servicio 2 info");
        Mockito.when(solicitudRepository.findByCodigoTrazabilidad(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenAnswer(invocation -> {
            SolicitudModel model = invocation.getArgument(0);
            model.setId(100L);
            return model;
        });

        SolicitudModel resultado = solicitudService.crear(solicitud);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(100L, resultado.getId());
        Assertions.assertNotNull(resultado.getCodigoTrazabilidad());
        Assertions.assertEquals(SolicitudEstados.PENDIENTE, resultado.getEstado());
        Mockito.verify(solicitudRepository, Mockito.times(1)).save(Mockito.any(SolicitudModel.class));
    }

    @Test
    void givenSolicitudWithDuplicateCode_whenCrear_thenThrowBusinessException() {
        SolicitudModel solicitud = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .codigoTrazabilidad("TR-DUP")
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(servicioClient.obtenerServicio(2L)).thenReturn("Servicio 2 info");
        Mockito.when(solicitudRepository.findByCodigoTrazabilidad("TR-DUP")).thenReturn(Optional.of(solicitud));

        Assertions.assertThrows(BusinessException.class, () -> solicitudService.crear(solicitud));
    }

    @Test
    void givenExistingSolicitud_whenActualizar_thenReturnActualizada() {
        SolicitudModel solicitud = SolicitudModel.builder().id(100L).idCliente(1L).build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente info");
        Mockito.when(solicitudRepository.findById(100L)).thenReturn(Optional.of(solicitud));
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenReturn(solicitud);

        SolicitudModel resultado = solicitudService.actualizar(solicitud);

        Assertions.assertNotNull(resultado);
        Mockito.verify(solicitudRepository, Mockito.times(1)).save(solicitud);
    }

    @Test
    void givenNonExistingSolicitud_whenActualizar_thenThrowResourceNotFoundException() {
        SolicitudModel solicitud = SolicitudModel.builder().id(999L).idCliente(1L).build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente info");
        Mockito.when(solicitudRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> solicitudService.actualizar(solicitud));
    }

    @Test
    void givenExistingId_whenEliminar_thenDeleteSuccessfully() {
        Long id = 1L;
        Mockito.when(solicitudRepository.existsById(id)).thenReturn(true);

        solicitudService.eliminar(id);

        Mockito.verify(solicitudRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void givenNonExistingId_whenEliminar_thenThrowResourceNotFoundException() {
        Long id = 999L;
        Mockito.when(solicitudRepository.existsById(id)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> solicitudService.eliminar(id));
    }

    @Test
    void givenPageable_whenListarTodas_thenReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAll(pageable)).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.listarTodas(pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void givenPendingSolicitud_whenCancelar_thenReturnCancelada() {
        Long id = 10L;
        Long idCliente = 1L;
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(id)
                .idCliente(idCliente)
                .codigoTrazabilidad("TR-ABC")
                .estado(SolicitudEstados.PENDIENTE)
                .build();

        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        Mockito.when(asignacionRepository.existsByIdSolicitud(id)).thenReturn(false);
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenAnswer(invocation -> {
            SolicitudModel model = invocation.getArgument(0);
            return model;
        });

        SolicitudModel resultado = solicitudService.cancelar(id, idCliente);

        Assertions.assertEquals(SolicitudEstados.CANCELADA, resultado.getEstado());
        Mockito.verify(notificationService, Mockito.times(1)).enviarNotificacion(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void givenAssignedSolicitud_whenCancelar_thenThrowBusinessException() {
        Long id = 10L;
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(id)
                .idCliente(1L)
                .estado(SolicitudEstados.PENDIENTE)
                .build();

        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        Mockito.when(asignacionRepository.existsByIdSolicitud(id)).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, () -> solicitudService.cancelar(id, 1L));
    }

    @Test
    void givenProcessedSolicitud_whenCancelar_thenThrowBusinessException() {
        Long id = 10L;
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(id)
                .idCliente(1L)
                .estado("Resuelto")
                .build();

        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));

        Assertions.assertThrows(BusinessException.class, () -> solicitudService.cancelar(id, 1L));
    }

    @Test
    void givenDifferentOwner_whenCancelar_thenThrowBusinessException() {
        Long id = 10L;
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(id)
                .idCliente(1L)
                .estado(SolicitudEstados.PENDIENTE)
                .build();

        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));

        Assertions.assertThrows(BusinessException.class, () -> solicitudService.cancelar(id, 99L));
    }

    @Test
    void givenExistingId_whenObtenerPorId_thenReturnSolicitud() {
        Long id = 1L;
        SolicitudModel solicitud = SolicitudModel.builder().id(id).build();
        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));

        SolicitudModel resultado = solicitudService.obtenerPorId(id);

        Assertions.assertEquals(id, resultado.getId());
    }

    @Test
    void givenNonExistingId_whenObtenerPorId_thenThrowResourceNotFoundException() {
        Long id = 999L;
        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> solicitudService.obtenerPorId(id));
    }

    @Test
    void givenClienteIdAndPageable_whenBuscarPorCliente_thenReturnPage() {
        Long idCliente = 55L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAllByIdCliente(idCliente, pageable)).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.buscarPorCliente(idCliente, pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void givenEstadoAndPageable_whenBuscarPorEstado_thenReturnPage() {
        String estado = "Pendiente";
        Pageable pageable = PageRequest.of(0, 10);
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAllByEstado(estado, pageable)).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.buscarPorEstado(estado, pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }
}