package com.traceability.solicitudes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asignaciones")
public class AsignacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Long id;

    @Column(name = "id_solicitud", nullable = false)
    private Long idSolicitud;

    @Column(name = "id_consultor", nullable = false)
    private Long idConsultor;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAsignacion == null) {
            this.fechaAsignacion = LocalDateTime.now();
        }
    }
}