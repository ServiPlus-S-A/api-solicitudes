package com.traceability.solicitudes.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class S3StorageConfigTest {

    @Test
    void shouldCreateConfigInstance() {

        S3StorageConfig config =
                new S3StorageConfig();

        assertThat(config)
                .isNotNull();
    }
}