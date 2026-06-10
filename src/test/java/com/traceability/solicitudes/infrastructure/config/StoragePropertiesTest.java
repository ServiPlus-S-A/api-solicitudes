package com.traceability.solicitudes.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StoragePropertiesTest {

    @Test
    void shouldExposeValues() {

        StorageProperties properties =
                new StorageProperties(
                        "http://localhost:9000",
                        "us-east-1",
                        "bucket-test",
                        "access",
                        "secret");

        assertThat(properties.endpoint()).isEqualTo("http://localhost:9000");
        assertThat(properties.region()).isEqualTo("us-east-1");
        assertThat(properties.bucket()).isEqualTo("bucket-test");
        assertThat(properties.accessKey()).isEqualTo("access");
        assertThat(properties.secretKey()).isEqualTo("secret");
    }
}