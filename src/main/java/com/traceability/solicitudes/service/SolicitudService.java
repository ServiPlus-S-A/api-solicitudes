package com.traceability.solicitudes.service;

import com.traceability.solicitudes.model.SolicitudModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz de negocio para la gestión de solicitudes.
 */
public interface SolicitudService {

    /**
     * Registra una nueva solicitud.
     * @param solicitud entidad de la solicitud a crear
     * @return la solicitud creada
     */
    SolicitudModel crear(SolicitudModel solicitud);

    /**
     * Modifica una solicitud existente.
     * @param solicitud datos actualizados de la solicitud
     * @return la solicitud actualizada
     */
    SolicitudModel actualizar(SolicitudModel solicitud);

    /**
     * Elimina físicamente una solicitud del sistema.
     * @param id identificador único de la solicitud
     */
    void eliminar(Long id);

    /**
     * Retorna todas las solicitudes registradas con paginación.
     * @param pageable opciones de paginación
     * @return página de solicitudes
     */
    Page<SolicitudModel> obtenerTodos(Pageable pageable);

    /**
     * Busca y retorna una solicitud específica.
     * @param id identificador único de la solicitud
     * @return la solicitud encontrada
     */
    SolicitudModel obtenerPorId(Long id);

    /**
     * Busca todas las solicitudes asociadas a un cliente.
     * @param idCliente identificador del cliente
     * @param pageable opciones de paginación
     * @return página de solicitudes
     */
    Page<SolicitudModel> buscarPorCliente(Long idCliente, Pageable pageable);

    /**
     * Busca solicitudes filtradas por su estado.
     * @param estado estado a filtrar
     * @param pageable opciones de paginación
     * @return página de solicitudes
     */
    Page<SolicitudModel> buscarPorEstado(String estado, Pageable pageable);
}
