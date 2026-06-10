package com.traceability.solicitudes.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.traceability.solicitudes.domain.model.Solicitud;
import com.traceability.solicitudes.domain.model.SolicitudEstado;
import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.SolicitudEntity;
import com.traceability.solicitudes.infrastructure.persistence.jpa.repository.SolicitudJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class JpaSolicitudRepositoryAdapterTest {

    @Mock
    private SolicitudJpaRepository repository;

    @InjectMocks
    private JpaSolicitudRepositoryAdapter adapter;

    private Solicitud buildSolicitud() {
        // Usamos el truco dinámico del enum por seguridad
        SolicitudEstado estado = SolicitudEstado.values()[0];

        return new Solicitud(
                UUID.randomUUID(),
                "Titulo",
                "Descripcion",
                "user-1",
                estado,
                Instant.now(),
                Instant.now());
    }

    @Test
    void shouldSaveSolicitud() {
        Solicitud solicitud = buildSolicitud();

        when(repository.save(org.mockito.ArgumentMatchers.any()))
                .thenReturn(SolicitudEntity.fromDomain(solicitud));

        Solicitud result = adapter.save(solicitud);

        assertThat(result.getId()).isEqualTo(solicitud.getId());
    }

    @Test
    void shouldFindById() {
        Solicitud solicitud = buildSolicitud();

        when(repository.findById(solicitud.getId()))
                .thenReturn(Optional.of(SolicitudEntity.fromDomain(solicitud)));

        var result = adapter.findById(solicitud.getId());

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindAll() {
        Solicitud solicitud = buildSolicitud();
        List<SolicitudEntity> entities = List.of(SolicitudEntity.fromDomain(solicitud));

        // Creamos una página de Spring Data con la entidad de prueba
        Page<SolicitudEntity> pageResult = new PageImpl<>(entities);

        // Mockeamos el método esperando que Spring Data maneje la paginación con Page o listado interno
        // Si tu SolicitudJpaRepository retorna List en lugar de Page, Mockito igual aceptará any() con la respuesta adecuada
        when(repository.findAllByOrderByCreadoEnDesc(org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenAnswer(invocation -> {
                    // Este truco dinámico funciona tanto si el repositorio original retorna List como si retorna Page
                    return pageResult;
                });

        var result = adapter.findAll(0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldCount() {
        when(repository.count()).thenReturn(5L);

        // Ajustamos la aserción a long (5L) para evitar conflictos de tipo primitivo
        assertThat(adapter.count()).isEqualTo(5L);
    }

    @Test
    void shouldCountByEstado() {
        SolicitudEstado estado = SolicitudEstado.values()[0];

        when(repository.countByEstado(estado)).thenReturn(3L);

        assertThat(adapter.countByEstado(estado)).isEqualTo(3L);
    }
}