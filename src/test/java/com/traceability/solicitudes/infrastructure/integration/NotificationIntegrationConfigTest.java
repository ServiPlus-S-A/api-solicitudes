package com.traceability.solicitudes.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.MessageChannel;

class NotificationIntegrationConfigTest {

    private final NotificationIntegrationConfig config =
            new NotificationIntegrationConfig();

    @Test
    void shouldCreateSmtpOutboundChannel() {

        MessageChannel channel =
                config.smtpOutboundChannel();

        assertThat(channel).isNotNull();
    }
}