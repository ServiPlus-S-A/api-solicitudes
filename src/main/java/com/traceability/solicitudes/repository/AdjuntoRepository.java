package com.traceability.solicitudes.repository;

import com.traceability.solicitudes.model.AdjuntoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdjuntoRepository extends JpaRepository<AdjuntoModel, Long> {
    List<AdjuntoModel> findByIdSolicitud(Long idSolicitud);
}