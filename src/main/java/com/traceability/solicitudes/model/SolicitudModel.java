package com.traceability.solicitudes.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitudes")
public class SolicitudModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_tipo_servicio", nullable = false)
    private Long idTipoServicio;

    @Column(name = "asunto", nullable = false, length = 100)
    private String asunto;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "codigo_trazabilidad", unique = true, length = 20)
    private String codigoTrazabilidad;

    @Column(name = "ubicacion", nullable = false, length = 100)
    private String ubicacion;

    @Builder.Default
    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdjuntoModel> adjuntos = new ArrayList<>();


    /**
     * Método de ciclo de vida de JPA que se ejecuta antes de persistir la entidad.
     * Asigna la fecha de apertura actual en la zona horaria de Bogotá si no ha sido establecida.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaApertura == null) {
            this.fechaApertura = LocalDateTime.now(ZoneId.of("America/Bogota"));
        }
    }

    /**
     * Agrega un archivo adjunto a la lista de la solicitud de forma bidireccional.
     * Asegura que tanto la solicitud conozca el adjunto como el adjunto apunte a esta solicitud.
     *
     * @param adjunto el modelo del archivo adjunto a vincular
     */
    public void addAdjunto(AdjuntoModel adjunto) {
        if (adjuntos == null) {
            adjuntos = new ArrayList<>();
        }
        adjuntos.add(adjunto);
        adjunto.setSolicitud(this);
    }
}