package com.traceability.solicitudes.service;

import com.traceability.solicitudes.exception.BusinessException;
import com.traceability.solicitudes.exception.ResourceNotFoundException;
import com.traceability.solicitudes.integration.ClienteClient;
import com.traceability.solicitudes.integration.ServicioClient;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.repository.SolicitudRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación concreta de SolicitudService.
 * Utiliza inyección de dependencias basada en constructores con Lombok y anotaciones de caché y resiliencia.
 */
@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudServiceImpl.class);

    private final SolicitudRepository solicitudRepository;
    private final ClienteClient clienteClient;
    private final ServicioClient servicioClient;
    private final NotificationService notificationService;
    private final MetricService metricService;

    @Override
    @Transactional
    @CircuitBreaker(name = "solicitudService")
    public SolicitudModel crear(final SolicitudModel solicitud) {
        log.info("Procesando creación de solicitud para cliente ID: {}", solicitud.getIdCliente());
        
        // Llamadas de integración externas con protección de resiliencia
        String clienteInfo = clienteClient.obtenerCliente(solicitud.getIdCliente());
        String servicioInfo = servicioClient.obtenerServicio(solicitud.getIdTipoServicio());
        log.info("Validación externa exitosa: {} | {}", clienteInfo, servicioInfo);

        if (solicitud.getCodigoTrazabilidad() == null || solicitud.getCodigoTrazabilidad().isBlank()) {
            solicitud.setCodigoTrazabilidad("TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        if (solicitudRepository.findByCodigoTrazabilidad(solicitud.getCodigoTrazabilidad()).isPresent()) {
            metricService.incrementarContador("solicitudes.crear.error.duplicado");
            throw new BusinessException(
                "Ya existe una solicitud con el código de trazabilidad: " + solicitud.getCodigoTrazabilidad()
            );
        }

        solicitud.setFechaApertura(LocalDateTime.now());
        if (solicitud.getEstado() == null) {
            solicitud.setEstado("Pendiente");
        }

        SolicitudModel creada = solicitudRepository.save(solicitud);
        
        metricService.incrementarContador("solicitudes.creadas");
        String usuarioActual = obtenerUsuarioActual();
        
        notificationService.enviarNotificacion(
                "cliente_" + creada.getIdCliente() + "@traceability.com",
                "Registro de Solicitud Exitosa",
                "Su solicitud con el código de trazabilidad " + creada.getCodigoTrazabilidad() + " ha sido guardada."
        );

        return creada;
    }

    @Override
    @Transactional
    @CacheEvict(value = "solicitudes", key = "'solicitud:' + #solicitud.id")
    @CircuitBreaker(name = "solicitudService")
    public SolicitudModel actualizar(final SolicitudModel solicitud) {
        log.info("Actualizando solicitud ID: {}", solicitud.getId());
        
        // Llamada de integración externa para validar cliente en la actualización
        String clienteInfo = clienteClient.obtenerCliente(solicitud.getIdCliente());
        log.info("Validación externa cliente actualización: {}", clienteInfo);

        SolicitudModel existente = solicitudRepository.findById(solicitud.getId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Solicitud con id " + solicitud.getId() + " no encontrada"
            ));

        solicitud.setCodigoTrazabilidad(existente.getCodigoTrazabilidad());
        solicitud.setFechaApertura(existente.getFechaApertura());

        SolicitudModel actualizada = solicitudRepository.save(solicitud);
        
        metricService.incrementarContador("solicitudes.actualizadas");
        String usuarioActual = obtenerUsuarioActual();

        notificationService.enviarNotificacion(
                "cliente_" + actualizada.getIdCliente() + "@traceability.com",
                "Solicitud Actualizada",
                "Su solicitud " + actualizada.getCodigoTrazabilidad() + " ha sido actualizada."
        );

        return actualizada;
    }

    @Override
    @Transactional
    @CacheEvict(value = "solicitudes", key = "'solicitud:' + #id")
    public void eliminar(final Long id) {
        log.info("Eliminando solicitud ID: {}", id);
        if (!solicitudRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitud con id " + id + " no encontrada");
        }
        solicitudRepository.deleteById(id);
        
        metricService.incrementarContador("solicitudes.eliminadas");
        String usuarioActual = obtenerUsuarioActual();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudModel> obtenerTodos(Pageable pageable) {
        log.info("Consultando todas las solicitudes paginadas: {}", pageable);
        return solicitudRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "solicitudes", key = "'solicitud:' + #id")
    public SolicitudModel obtenerPorId(Long id) {
        log.info("Buscando solicitud ID: {} (Fallo de caché L1/L2)", id);
        return solicitudRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudModel> buscarPorCliente(Long idCliente, Pageable pageable) {
        log.info("Consultando solicitudes por cliente ID {} paginado", idCliente);
        return solicitudRepository.findAllByIdCliente(idCliente, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudModel> buscarPorEstado(String estado, Pageable pageable) {
        log.info("Consultando solicitudes por estado '{}' paginado", estado);
        return solicitudRepository.findAllByEstado(estado, pageable);
    }

    private String obtenerUsuarioActual() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return "sistema";
    }
}
