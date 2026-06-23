package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.model.AsignacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para realizar operaciones sobre las asignaciones en la base de datos.
 */
@Repository
public interface AsignacionRepository extends JpaRepository<AsignacionModel, Long> {

    /**
     * Busca de manera automática todas las asignaciones pertenecientes a una solicitud.
     * @param idSolicitud identificador de la solicitud objetivo
     * @return lista de asignaciones de la solicitud
     */
    List<AsignacionModel> findByIdSolicitud(Long idSolicitud);
}