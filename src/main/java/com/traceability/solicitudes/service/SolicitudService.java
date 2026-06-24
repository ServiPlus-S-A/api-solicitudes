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
import com.traceability.solicitudes.repository.SolicitudRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ClienteClient clienteClient;
    private final ServicioClient servicioClient;
    private final NotificationService notificationService;
    private final MetricService metricService;
    private final SolicitudMapper solicitudMapper;

    /**
     * Procesa la creación de una nueva solicitud validando clientes y servicios remotos.
     * Genera automáticamente un código único de trazabilidad si no se suministra uno.
     *
     * @param  dto Modelo inicial enviado para el registro
     * @return SolicitudModel guardada con éxito en la base de datos
     * @throws BusinessException si el código de trazabilidad generado ya está registrado
     */
    @Transactional
    @CircuitBreaker(name = "solicitudService")
    public SolicitudResponseDTO crear(final SolicitudRequestDTO dto) {
        log.info("Procesando creación de solicitud para cliente ID: {}", dto.getIdCliente());

        String clienteInfo = clienteClient.obtenerCliente(dto.getIdCliente());
        String servicioInfo = servicioClient.obtenerServicio(dto.getIdTipoServicio());
        log.info("Validación externa exitosa: {} | {}", clienteInfo, servicioInfo);

        SolicitudModel solicitud = solicitudMapper.toEntity(dto);

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
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        }

        SolicitudModel creada = solicitudRepository.save(solicitud);
        metricService.incrementarContador("solicitudes.creadas");

        notificationService.enviarNotificacion(
                "cliente_" + creada.getIdCliente() + "@traceability.com",
                "Registro de Solicitud Exitosa",
                "Su solicitud con el código de trazabilidad " + creada.getCodigoTrazabilidad() + " ha sido guardada."
        );

        return solicitudMapper.toResponse(creada);
    }

    /**
     * Actualiza la información y el estado de una solicitud existente en el sistema.
     * Desaloja el valor previo almacenado en la caché "solicitudes".
     *
     * @param solicitud Entidad con los datos modificados para actualizar
     * @return SolicitudModel actualizada y guardada
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
     * Obtiene una lista paginada de todas las solicitudes registradas.
     * Blinda la ejecución contra ordenamientos vacíos o corruptos de Swagger.
     *
     * @param pageable Configuración de paginación
     * @return Página de resultados estructurada en SolicitudModel
     */
    @Transactional(readOnly = true)
    public Page<SolicitudModel> obtenerTodos(final Pageable pageable) {
        log.info("Consultando todas las solicitudes paginadas: {}", pageable);
        return solicitudRepository.findAll(sanearPageable(pageable));
    }

    /**
     * Recupera una solicitud específica por su ID.
     *
     * @param id Identificador único de búsqueda
     * @return SolicitudModel correspondiente
     */
    @Transactional(readOnly = true)
    public SolicitudModel obtenerPorId(final Long id) {
        log.info("Buscando solicitud ID: {}", id);
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con id: " + id));
    }

    /**
     * Busca y pagina solicitudes asociadas a un cliente específico de manera segura.
     *
     * @param idCliente Identificador de cliente para el filtrado
     * @param pageable  Configuración de paginación
     * @return Página con las solicitudes que coinciden con el cliente
     */
    @Transactional(readOnly = true)
    public Page<SolicitudModel> buscarPorCliente(final Long idCliente, final Pageable pageable) {
        log.info("Consultando solicitudes por cliente ID {} paginado", idCliente);
        return solicitudRepository.findAllByIdCliente(idCliente, sanearPageable(pageable));
    }

    /**
     * Filtra y obtiene solicitudes basadas en su estado actual de manera segura.
     * Convierte el parámetro de texto al ENUM correspondiente y sanea la paginación.
     *
     * @param estado   Estado a buscar en formato String (ej: 'pendiente', 'en_proceso')
     * @param pageable Configuración de paginación
     * @return Página con los resultados filtrados por estado
     */
    @Transactional(readOnly = true)
    public Page<SolicitudModel> buscarPorEstado(final String estado, final Pageable pageable) {
        log.info("Consultando solicitudes por estado '{}' paginado", estado);

        // 1. Convertimos el String de forma segura al ENUM mapeado.
        // Si mandan basura que no existe en el Enum,
        // arrojamos un error de negocio controlado (Evita el 500)
        EstadoSolicitud estadoEnum;
        try {
            estadoEnum = EstadoSolicitud.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("El estado suministrado '" + estado +
                    "' no es un estado válido en el sistema.");
        }

        // 2. Enviamos el ENUM real y el pageable saneado al repositorio
        // NOTA: Si tu repositorio recibe un String, cambia 'estadoEnum' por 'estadoEnum.name()'
        return solicitudRepository.findAllByEstado(estadoEnum, sanearPageable(pageable));
    }

    /**
     * Método utilitario privado para sanear el Pageable y evitar que parámetros corruptos
     * u ordenamientos vacíos ("string", "[]") rompan las consultas SQL.
     * Cumple estrictamente con los estándares de SonarCloud.
     */
    private Pageable sanearPageable(final Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 10, Sort.by("id").descending());
        }

        // 1. LISTA BLANCA: Solo estos campos reales de tu SolicitudModel pueden ser ordenados
        final java.util.Set<String> camposPermitidos = java.util.Set.of(
                "id", "idCliente", "idTipoServicio", "asunto",
                "descripcion", "estado", "fechaApertura", "codigoTrazabilidad", "ubicacion"
        );

        // 2. Evaluamos si el cliente está intentando ordenar por algo que NO está en la lista blanca
        boolean tieneOrdenInvalido = pageable.getSort().stream()
                .anyMatch(order -> !camposPermitidos.contains(order.getProperty()));

        // 3. Si no hay orden o detectamos basura como [""] o "string", forzamos el orden seguro por ID
        if (pageable.getSort().isUnsorted() || tieneOrdenInvalido) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());
        }

        return pageable;
    }
}