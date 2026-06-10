package com.traceability.solicitudes.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.traceability.solicitudes.domain.model.ArchivoAdjunto;
import com.traceability.solicitudes.infrastructure.persistence.jpa.entity.ArchivoAdjuntoEntity;
import com.traceability.solicitudes.infrastructure.persistence.jpa.repository.ArchivoAdjuntoJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFileRepositoryAdapterTest {

    @Mock
    private ArchivoAdjuntoJpaRepository repository;

    @InjectMocks
    private JpaFileRepositoryAdapter adapter;

    private ArchivoAdjunto buildArchivo() {

        return new ArchivoAdjunto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "archivo.pdf",
                "application/pdf",
                "key",
                Instant.now());
    }

    @Test
    void shouldSaveArchivo() {

        ArchivoAdjunto archivo = buildArchivo();

        when(repository.save(org.mockito.ArgumentMatchers.any()))
                .thenReturn(ArchivoAdjuntoEntity.fromDomain(archivo));

        ArchivoAdjunto result = adapter.save(archivo);

        assertThat(result.getId()).isEqualTo(archivo.getId());
    }

    @Test
    void shouldFindById() {

        ArchivoAdjunto archivo = buildArchivo();

        when(repository.findById(archivo.getId()))
                .thenReturn(
                        Optional.of(
                                ArchivoAdjuntoEntity.fromDomain(archivo)));

        var result =
                adapter.findById(archivo.getId());

        assertThat(result).isPresent();
    }

    @Test
    void shouldFindBySolicitudId() {

        ArchivoAdjunto archivo = buildArchivo();

        when(repository.findBySolicitudId(
                archivo.getSolicitudId()))
                .thenReturn(
                        List.of(
                                ArchivoAdjuntoEntity.fromDomain(
                                        archivo)));

        var result =
                adapter.findBySolicitudId(
                        archivo.getSolicitudId());

        assertThat(result).hasSize(1);
    }
}