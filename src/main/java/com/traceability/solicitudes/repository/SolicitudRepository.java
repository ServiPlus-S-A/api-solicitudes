package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.model.SolicitudModel;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la administración de solicitudes en la base de datos.
 */
@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudModel, Long> {

    /**
     * Busca todas las solicitudes de un cliente de forma paginada.
     * @param idCliente identificador del cliente
     * @param pageable opciones de paginación
     * @return página de solicitudes encontradas
     */
    Page<SolicitudModel> findAllByIdCliente(Long idCliente, Pageable pageable);

    /**
     * Busca todas las solicitudes de un tipo de servicio de forma paginada.
     * @param idTipoServicio identificador del tipo de servicio
     * @param pageable opciones de paginación
     * @return página de solicitudes encontradas
     */
    Page<SolicitudModel> findAllByIdTipoServicio(Long idTipoServicio, Pageable pageable);

    /**
     * Busca una solicitud por su código de trazabilidad único.
     * @param codigoTrazabilidad código a buscar
     * @return opcional con la solicitud si existe
     */
    Optional<SolicitudModel> findByCodigoTrazabilidad(String codigoTrazabilidad);

    /**
     * Busca todas las solicitudes en un estado específico de forma paginada.
     * @param estado estado de las solicitudes
     * @param pageable opciones de paginación
     * @return página de solicitudes encontradas
     */
    Page<SolicitudModel> findAllByEstado(String estado, Pageable pageable);
}
