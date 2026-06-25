package com.traceability.solicitudes.controller;

import com.traceability.solicitudes.dto.SolicitudMapper;
import com.traceability.solicitudes.dto.SolicitudRequestDTO;
import com.traceability.solicitudes.dto.SolicitudResponseDTO;
import com.traceability.solicitudes.model.SolicitudModel;
import com.traceability.solicitudes.service.SolicitudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de solicitudes.
 */
@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Operaciones de CRUD de solicitudes para TraceAbility")
public class SolicitudController {

    private static final Logger log = LoggerFactory.getLogger(SolicitudController.class);

    private final SolicitudService solicitudService;
    private final SolicitudMapper solicitudMapper;

    /**
     * Endpoint para crear una solicitud.
     * @param requestDTO datos de entrada
     * @return DTO de respuesta con la solicitud creada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Crear solicitud", description = "Registra una nueva solicitud en el sistema")
    @ApiResponse(responseCode = "201", description = "Solicitud creada con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Conflicto por código de trazabilidad duplicado")
    public ResponseEntity<SolicitudResponseDTO> crear(@Valid @RequestBody final SolicitudRequestDTO requestDTO) {
        log.info("REST solicitud para crear recurso por cliente: {}", requestDTO.getIdCliente());

        SolicitudResponseDTO respuesta = solicitudService.crear(requestDTO);

        log.info("REST solicitud completada con éxito");
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Endpoint para actualizar una solicitud.
     * @param id identificador
     * @param requestDTO datos modificados
     * @return DTO de respuesta
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @Operation(summary = "Actualizar solicitud", description = "Modifica una solicitud existente")
    @ApiResponse(responseCode = "200", description = "Solicitud actualizada con éxito")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<SolicitudResponseDTO> actualizar(
            @PathVariable final Long id,
            @Valid @RequestBody final SolicitudRequestDTO requestDTO) {
        log.info("REST solicitud para actualizar ID: {}", id);
        SolicitudModel entity = solicitudMapper.toEntity(requestDTO);
        entity.setId(id);
        SolicitudModel actualizada = solicitudService.actualizar(entity);
        log.info("REST solicitud de actualización finalizada para ID {}", id);
        return ResponseEntity.ok(solicitudMapper.toResponse(actualizada));
    }

    /**
     * Endpoint para eliminar una solicitud.
     * @param id identificador
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar solicitud", description = "Elimina físicamente una solicitud del sistema")
    @ApiResponse(responseCode = "244", description = "Solicitud eliminada con éxito")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<Void> eliminar(@PathVariable final Long id) {
        log.info("REST solicitud para eliminar ID: {}", id);
        solicitudService.eliminar(id);
        log.info("REST solicitud de eliminación completada para ID {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para listar todas las solicitudes con paginación.
     * @param pageable opciones de paginación
     * @return página de DTOs de respuesta
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @Operation(summary = "Listar solicitudes", description = "Obtiene todas las solicitudes de forma paginada")
    @ApiResponse(responseCode = "200", description = "Listado de solicitudes devuelto exitosamente")
    public ResponseEntity<Page<SolicitudResponseDTO>> obtenerTodos(
            @PageableDefault(size = 10) final Pageable pageable) {
        log.info("REST solicitud de consulta de todas las solicitudes con paginación: {}", pageable);
        Page<SolicitudModel> pagina = solicitudService.obtenerTodos(pageable);
        return ResponseEntity.ok(pagina.map(solicitudMapper::toResponse));
    }

    /**
     * Endpoint para obtener una solicitud por su ID.
     * @param id identificador
     * @return DTO de respuesta
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'TECNICO', 'ADMIN')")
    @Operation(summary = "Obtener solicitud por ID", description = "Retorna una solicitud en base a su ID")
    @ApiResponse(responseCode = "200", description = "Detalles de la solicitud obtenidos exitosamente")
    @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    public ResponseEntity<SolicitudResponseDTO> obtenerPorId(@PathVariable final Long id) {
        log.info("REST solicitud para buscar por ID: {}", id);
        SolicitudModel entity = solicitudService.obtenerPorId(id);
        return ResponseEntity.ok(solicitudMapper.toResponse(entity));
    }

    /**
     * Endpoint para buscar solicitudes asociadas a un cliente.
     * @param idCliente identificador del cliente
     * @param pageable opciones de paginación
     * @return página de DTOs de respuesta
     */
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Buscar por cliente", description = "Listado de solicitudes asociadas a un ID de cliente")
    @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente")
    public ResponseEntity<Page<SolicitudResponseDTO>> buscarPorCliente(
            @PathVariable final Long idCliente,
            @PageableDefault(size = 10) final Pageable pageable) {
        log.info("REST solicitud para buscar por cliente ID {}: {}", idCliente, pageable);
        Page<SolicitudModel> pagina = solicitudService.buscarPorCliente(idCliente, pageable);
        return ResponseEntity.ok(pagina.map(solicitudMapper::toResponse));
    }

    /**
     * Endpoint para buscar solicitudes por su estado.
     * @param estado estado a filtrar
     * @param pageable opciones de paginación
     * @return página de DTOs de respuesta
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('TECNICO', 'ADMIN')")
    @Operation(summary = "Buscar por estado", description = "Listado de solicitudes en un estado específico")
    @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente")
    public ResponseEntity<Page<SolicitudResponseDTO>> buscarPorEstado(
            @PathVariable final String estado,
            @PageableDefault(size = 10) final Pageable pageable) {
        log.info("REST solicitud para buscar por estado '{}': {}", estado, pageable);
        Page<SolicitudModel> pagina = solicitudService.buscarPorEstado(estado, pageable);
        return ResponseEntity.ok(pagina.map(solicitudMapper::toResponse));
    }
}
