package com.traceability.solicitudes.infrastructure.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.traceability.solicitudes.infrastructure.config.MailProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageChannel;

@ExtendWith(MockitoExtension.class)
class MailSmtpNotificationAdapterTest {

    @Mock
    private MessageChannel channel;

    @Test
    void shouldSendMailMessage() {

        MailProperties properties =
                new MailProperties(
                        "from@test.com",
                        "Solicitud creada");

        MailSmtpNotificationAdapter adapter =
                new MailSmtpNotificationAdapter(
                        channel,
                        properties);

        adapter.sendSolicitudCreated(
                "dest@test.com",
                "123",
                "Titulo");

        verify(channel).send(any());
    }
}