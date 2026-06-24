package com.traceability.solicitudes.service;

import com.traceability.solicitudes.exception.BusinessException;
import com.traceability.solicitudes.exception.ResourceNotFoundException;
import com.traceability.solicitudes.integration.ClienteClient;
import com.traceability.solicitudes.integration.ServicioClient;
import com.traceability.solicitudes.model.SolicitudEstados;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.repository.AsignacionRepository;
import com.traceability.solicitudes.repository.SolicitudRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Servicio de negocio exclusivo para la gestión de solicitudes.
 * Diseñado sin interfaces redundantes siguiendo la inyección de dependencias nativa de Spring.
 */
@Service
@RequiredArgsConstructor
public class SolicitudService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);

    private final SolicitudRepository solicitudRepository;
    private final AsignacionRepository asignacionRepository;
    private final ClienteClient clienteClient;
    private final ServicioClient servicioClient;
    private final NotificationService notificationService;
    private final MetricService metricService;

    /**
     * Procesa la creación de una nueva solicitud validando clientes y servicios remotos.
     * Genera automáticamente un código único de trazabilidad si no se suministra uno.
     *
     * @param solicitud Modelo inicial enviado para el registro
     * @return SolicitudModel guardada con éxito en la base de datos
     * @throws BusinessException si el código de trazabilidad generado ya está registrado
     */
    @Transactional
    @CircuitBreaker(name = "solicitudService")
    public SolicitudModel crear(final SolicitudModel solicitud) {
        log.info("Procesando creación de solicitud para cliente ID: {}", solicitud.getIdCliente());

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

        solicitud.setFechaApertura(LocalDateTime.now(ZoneId.of("America/Bogota")));
        if (solicitud.getEstado() == null) {
            solicitud.setEstado(SolicitudEstados.PENDIENTE);
        }

        SolicitudModel creada = solicitudRepository.save(solicitud);
        metricService.incrementarContador("solicitudes.creadas");

        notificationService.enviarNotificacion(
                "cliente_" + creada.getIdCliente() + "@traceability.com",
                "Registro de Solicitud Exitosa",
                "Su solicitud con el código de trazabilidad " + creada.getCodigoTrazabilidad() + " ha sido guardada."
        );

        return creada;
    }

    /**
     * Actualiza la información y el estado de una solicitud existente en el sistema.
     * Desaloja el valor previo almacenado en la caché "solicitudes".
     *
     * @param solicitud Entidad con los datos modificados para actualizar
     * @return SolicitudModel actualizada y guardada
     * @throws ResourceNotFoundException si el identificador de la solicitud no existe
     */
    @Transactional
    @CacheEvict(value = "solicitudes", key = "'solicitud:' + #solicitud.id")
    @CircuitBreaker(name = "solicitudService")
    public SolicitudModel actualizar(final SolicitudModel solicitud) {
        log.info("Actualizando solicitud ID: {}", solicitud.getId());

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

        notificationService.enviarNotificacion(
                "cliente_" + actualizada.getIdCliente() + "@traceability.com",
                "Solicitud Actualizada",
                "Su solicitud " + actualizada.getCodigoTrazabilidad() + " ha sido actualizada."
        );

        return actualizada;
    }

    /**
     * Elimina el registro físico de una solicitud según su ID del sistema.
     * Desaloja también la caché asignada a dicho registro.
     *
     * @param id Identificador único de la solicitud a borrar
     * @throws ResourceNotFoundException si no se encuentra el ID
     */
    @Transactional
    @CacheEvict(value = "solicitudes", key = "'solicitud:' + #id")
    public void eliminar(final Long id) {
        log.info("Eliminando solicitud ID: {}", id);
        if (!solicitudRepository.existsById(id)) {
            throw new ResourceNotFoundException("Solicitud con id " + id + " no encontrada");
        }
        solicitudRepository.deleteById(id);
        metricService.incrementarContador("solicitudes.eliminadas");
    }

    /**
     * Obtiene el listado total de solicitudes sin restricciones de propiedad.
     * Destinado al rol de coordinador (ADMIN).
     *
     * @param pageable Configuración de paginación
     * @return Página de resultados estructurada en SolicitudModel
     */
    @Transactional(readOnly = true)
    public Page<SolicitudModel> listarTodas(Pageable pageable) {
        log.info("Consultando listado total de solicitudes (coordinador): {}", pageable);
        return solicitudRepository.findAll(pageable);
    }

    /**
     * Cancela una solicitud del cliente cuando aún se encuentra pendiente y sin asignación.
     *
     * @param id        Identificador de la solicitud
     * @param idCliente Identificador del cliente autenticado
     * @return SolicitudModel actualizada a estado Cancelada
     * @throws ResourceNotFoundException si la solicitud no existe
     * @throws BusinessException         si no cumple las reglas de cancelación
     */
    @Transactional
    @CacheEvict(value = "solicitudes", key = "'solicitud:' + #id")
    public SolicitudModel cancelar(final Long id, final Long idCliente) {
        log.info("Procesando cancelación de solicitud ID {} por cliente {}", id, idCliente);

        SolicitudModel existente = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud con id " + id + " no encontrada"
                ));

        if (!existente.getIdCliente().equals(idCliente)) {
            throw new BusinessException("Solo el cliente propietario puede cancelar la solicitud");
        }

        if (!SolicitudEstados.PENDIENTE.equalsIgnoreCase(existente.getEstado())) {
            throw new BusinessException(
                    "Solo se puede cancelar una solicitud en estado PENDIENTE. Estado actual: "
                            + existente.getEstado()
            );
        }

        if (asignacionRepository.existsByIdSolicitud(id)) {
            throw new BusinessException("No se puede cancelar una solicitud que ya fue asignada");
        }

        existente.setEstado(SolicitudEstados.CANCELADA);
        SolicitudModel cancelada = solicitudRepository.save(existente);
        metricService.incrementarContador("solicitudes.canceladas");

        notificationService.enviarNotificacion(
                "cliente_" + cancelada.getIdCliente() + "@traceability.com",
                "Solicitud Cancelada",
                "Su solicitud " + cancelada.getCodigoTrazabilidad() + " ha sido cancelada."
        );

        return cancelada;
    }

    /**
     * Recupera una solicitud específica por su ID. Utiliza caché de Spring (L1/L2).
     *
     * @param id Identificador único de búsqueda
     * @return SolicitudModel correspondiente
     * @throws ResourceNotFoundException si el recurso solicitado no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "solicitudes", key = "'solicitud:' + #id")
    public SolicitudModel obtenerPorId(Long id) {
        log.info("Buscando solicitud ID: {} (Fallo de caché L1/L2)", id);
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con id: " + id));
    }

    /**
     * Busca y pagina solicitudes asociadas a un cliente específico.
     *
     * @param idCliente Identificador de cliente para el filtrado
     * @param pageable  Configuración de paginación
     * @return Página con las solicitudes que coinciden con el cliente
     */
    @Transactional(readOnly = true)
    public Page<SolicitudModel> buscarPorCliente(Long idCliente, Pageable pageable) {
        log.info("Consultando solicitudes por cliente ID {} paginado", idCliente);
        return solicitudRepository.findAllByIdCliente(idCliente, pageable);
    }

    /**
     * Filtra y obtiene solicitudes basadas en su estado actual.
     *
     * @param estado   Estado a buscar (ej: 'Pendiente', 'Resuelto')
     * @param pageable Configuración de paginación
     * @return Página con los resultados filtrados por estado
     */
    @Transactional(readOnly = true)
    public Page<SolicitudModel> buscarPorEstado(String estado, Pageable pageable) {
        log.info("Consultando solicitudes por estado '{}' paginado", estado);
        return solicitudRepository.findAllByEstado(estado, pageable);
    }
}