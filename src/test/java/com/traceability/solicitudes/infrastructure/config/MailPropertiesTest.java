package com.traceability.solicitudes.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MailPropertiesTest {

    @Test
    void shouldExposeValues() {

        MailProperties properties =
                new MailProperties(
                        "noreply@test.com",
                        "Nueva solicitud");

        assertThat(properties.from())
                .isEqualTo("noreply@test.com");

        assertThat(properties.solicitudCreatedSubject())
                .isEqualTo("Nueva solicitud");
    }
}