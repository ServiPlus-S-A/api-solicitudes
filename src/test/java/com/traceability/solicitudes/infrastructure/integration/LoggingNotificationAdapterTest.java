package com.traceability.solicitudes.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class LoggingNotificationAdapterTest {

    @Test
    void shouldSendNotificationWithoutException() {

        LoggingNotificationAdapter adapter =
                new LoggingNotificationAdapter();

        assertThatCode(() ->
                adapter.sendSolicitudCreated(
                        "correo@test.com",
                        "123",
                        "Titulo"))
                .doesNotThrowAnyException();
    }
}