package com.traceability.solicitudes.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DomainModelTest {

    @Test
    void should_expose_solicitud_properties_and_equality() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Solicitud original =
                new Solicitud(id, "Titulo", "Descripcion", "user-1", SolicitudEstado.ENVIADA, now, now);

        assertThat(original.getId()).isEqualTo(id);
        assertThat(original.getTitulo()).isEqualTo("Titulo");
        assertThat(original.getDescripcion()).isEqualTo("Descripcion");
        assertThat(original.getSolicitanteId()).isEqualTo("user-1");
        assertThat(original.getEstado()).isEqualTo(SolicitudEstado.ENVIADA);
        assertThat(original.getCreadoEn()).isEqualTo(now);
        assertThat(original.getActualizadoEn()).isEqualTo(now);
        assertThat(original).isEqualTo(original);
        assertThat(original.hashCode()).isEqualTo(original.hashCode());

        Solicitud sameId = new Solicitud(id, "Otro", "Otro", "user-2", SolicitudEstado.BORRADOR, now, now);
        assertThat(original).isEqualTo(sameId);
        assertThat(original).isNotEqualTo(null);
        assertThat(original).isNotEqualTo("not-a-solicitud");
        assertThat(original).isNotEqualTo(new Solicitud(UUID.randomUUID(), "T", "D", "u", SolicitudEstado.ENVIADA, now, now));

        Solicitud updated = original.withEstado(SolicitudEstado.APROBADA, now.plusSeconds(5));
        assertThat(updated.getEstado()).isEqualTo(SolicitudEstado.APROBADA);
    }

    @Test
    void should_expose_metrica_resumen_getters() {
        MetricaResumen resumen = new MetricaResumen(1, 2, 3, 4);
        assertThat(resumen.getTotalSolicitudes()).isOne();
        assertThat(resumen.getSolicitudesPendientes()).isEqualTo(2);
        assertThat(resumen.getSolicitudesAprobadas()).isEqualTo(3);
        assertThat(resumen.getSolicitudesRechazadas()).isEqualTo(4);
    }

    @Test
    void should_expose_archivo_adjunto_properties_and_equality() {
        UUID id = UUID.randomUUID();
        UUID solicitudId = UUID.randomUUID();
        Instant now = Instant.now();
        ArchivoAdjunto archivo =
                new ArchivoAdjunto(id, solicitudId, "doc.pdf", "application/pdf", "storage-key", now);

        assertThat(archivo.getId()).isEqualTo(id);
        assertThat(archivo.getSolicitudId()).isEqualTo(solicitudId);
        assertThat(archivo.getNombreArchivo()).isEqualTo("doc.pdf");
        assertThat(archivo.getContentType()).isEqualTo("application/pdf");
        assertThat(archivo.getStorageKey()).isEqualTo("storage-key");
        assertThat(archivo.getSubidoEn()).isEqualTo(now);
        assertThat(archivo).isEqualTo(archivo);
        assertThat(archivo.hashCode()).isEqualTo(archivo.hashCode());

        ArchivoAdjunto sameId =
                new ArchivoAdjunto(id, UUID.randomUUID(), "other.pdf", "text/plain", "other", now);
        assertThat(archivo).isEqualTo(sameId);
        assertThat(archivo).isNotEqualTo(null);
        assertThat(archivo).isNotEqualTo("archivo");
        assertThat(archivo).isNotEqualTo(new ArchivoAdjunto(UUID.randomUUID(), solicitudId, "a", "b", "c", now));
    }

    @Test
    void should_contain_all_solicitud_estados() {
        assertThat(SolicitudEstado.values())
                .containsExactly(
                        SolicitudEstado.BORRADOR,
                        SolicitudEstado.ENVIADA,
                        SolicitudEstado.EN_REVISION,
                        SolicitudEstado.APROBADA,
                        SolicitudEstado.RECHAZADA);
    }
}
