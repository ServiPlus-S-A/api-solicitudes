package com.traceability.solicitudes.service;

import com.traceability.solicitudes.dto.SolicitudMapper;
import com.traceability.solicitudes.dto.SolicitudRequestDTO;
import com.traceability.solicitudes.dto.SolicitudResponseDTO;
import com.traceability.solicitudes.exception.BusinessException;
import com.traceability.solicitudes.exception.ResourceNotFoundException;
import com.traceability.solicitudes.integration.ClienteClient;
import com.traceability.solicitudes.integration.ServicioClient;
import com.traceability.solicitudes.model.EstadoSolicitud;
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
import org.springframework.data.domain.Sort;

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

    @Mock
    private SolicitudMapper solicitudMapper;

    @InjectMocks
    private SolicitudService solicitudService;

    @Test
    void givenValidSolicitud_whenCrear_thenReturnCreada() {
        SolicitudRequestDTO requestDTO = SolicitudRequestDTO.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .asunto("Soporte de Conexión")
                .descripcion("Problemas con la red principal")
                .estado("PENDIENTE")
                .build();

        SolicitudModel modelMapped = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .asunto("Soporte de Conexión")
                .descripcion("Problemas con la red principal")
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        SolicitudResponseDTO responseDTO = SolicitudResponseDTO.builder()
                .id(100L)
                .codigoTrazabilidad("TR-ABC12345")
                .estado(EstadoSolicitud.PENDIENTE.name())
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(servicioClient.obtenerServicio(2L)).thenReturn("Servicio 2 info");
        Mockito.when(solicitudMapper.toEntity(requestDTO)).thenReturn(modelMapped);
        Mockito.when(solicitudRepository.findByCodigoTrazabilidad(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenAnswer(invocation -> {
            SolicitudModel savedModel = invocation.getArgument(0);
            savedModel.setId(100L);
            return savedModel;
        });
        Mockito.when(solicitudMapper.toResponse(Mockito.any(SolicitudModel.class))).thenReturn(responseDTO);

        SolicitudResponseDTO resultado = solicitudService.crear(requestDTO);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(100L, resultado.getId());
        Assertions.assertNotNull(resultado.getCodigoTrazabilidad());
        Assertions.assertEquals(EstadoSolicitud.PENDIENTE.name(), resultado.getEstado());
        Mockito.verify(solicitudRepository, Mockito.times(1)).save(Mockito.any(SolicitudModel.class));
    }

    @Test
    void givenSolicitudWithDuplicateCode_whenCrear_thenThrowBusinessException() {
        SolicitudRequestDTO requestDTO = SolicitudRequestDTO.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .build();

        SolicitudModel modelMapped = SolicitudModel.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .codigoTrazabilidad("TR-DUP")
                .build();

        Mockito.when(clienteClient.obtenerCliente(1L)).thenReturn("Cliente 1 info");
        Mockito.when(servicioClient.obtenerServicio(2L)).thenReturn("Servicio 2 info");
        Mockito.when(solicitudMapper.toEntity(requestDTO)).thenReturn(modelMapped);
        Mockito.when(solicitudRepository.findByCodigoTrazabilidad("TR-DUP")).thenReturn(Optional.of(modelMapped));

        Assertions.assertThrows(BusinessException.class, () -> solicitudService.crear(requestDTO));
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
        Mockito.when(solicitudRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.listarTodas(pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void givenNullPageable_whenListarTodas_thenReturnPageWithDefaultSort() {
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.listarTodas(null);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void givenPageableWithInvalidSort_whenListarTodas_thenReturnPageWithDefaultSort() {
        Pageable pageableInvalido = PageRequest.of(0, 10, Sort.by("campoInexistente"));
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.listarTodas(pageableInvalido);

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
                .estado(EstadoSolicitud.PENDIENTE)
                .build();

        Mockito.when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        Mockito.when(asignacionRepository.existsByIdSolicitud(id)).thenReturn(false);
        Mockito.when(solicitudRepository.save(Mockito.any(SolicitudModel.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        SolicitudModel resultado = solicitudService.cancelar(id, idCliente);

        Assertions.assertEquals(EstadoSolicitud.CANCELADA, resultado.getEstado());
        Mockito.verify(notificationService, Mockito.times(1)).enviarNotificacion(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void givenAssignedSolicitud_whenCancelar_thenThrowBusinessException() {
        Long id = 10L;
        SolicitudModel solicitud = SolicitudModel.builder()
                .id(id)
                .idCliente(1L)
                .estado(EstadoSolicitud.PENDIENTE)
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
                .estado(EstadoSolicitud.COMPLETADA)
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
                .estado(EstadoSolicitud.PENDIENTE)
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
    void givenValidPageable_whenBuscarPorCliente_thenReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAllByIdCliente(
                Mockito.eq(1L), Mockito.any(Pageable.class))
        ).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.buscarPorCliente(1L, pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void givenValidEstado_whenBuscarPorEstado_thenReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<SolicitudModel> page = new PageImpl<>(Collections.singletonList(new SolicitudModel()));
        Mockito.when(solicitudRepository.findAllByEstado(
                Mockito.eq(EstadoSolicitud.PENDIENTE), Mockito.any(Pageable.class))
        ).thenReturn(page);

        Page<SolicitudModel> resultado = solicitudService.buscarPorEstado("PENDIENTE", pageable);

        Assertions.assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void givenInvalidEstado_whenBuscarPorEstado_thenThrowBusinessException() {
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertThrows(BusinessException.class,
                () -> solicitudService.buscarPorEstado("ESTADO_INVALIDO", pageable));
    }
}
