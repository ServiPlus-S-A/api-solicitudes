package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.model.AsignacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<AsignacionModel, Long> {
    // Convención de Spring Data: Busca automáticamente todas las asignaciones de una solicitud
    List<AsignacionModel> findByIdSolicitud(Long idSolicitud);
}