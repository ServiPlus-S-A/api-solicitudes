package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.model.AdjuntoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para realizar operaciones de persistencia en archivos adjuntos.
 */
@Repository
public interface AdjuntoRepository extends JpaRepository<AdjuntoModel, Long> {

    /**
     * Busca todos los archivos adjuntos vinculados a una solicitud específica.
     * @param idSolicitud identificador de la solicitud
     * @return lista de adjuntos encontrados
     */
    List<AdjuntoModel> findByIdSolicitud(Long idSolicitud);
}