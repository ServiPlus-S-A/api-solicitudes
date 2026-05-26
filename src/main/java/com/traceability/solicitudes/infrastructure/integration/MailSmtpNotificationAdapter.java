package com.traceability.solicitudes.infrastructure.integration;

import com.traceability.solicitudes.application.port.NotificationPort;
import com.traceability.solicitudes.infrastructure.config.MailProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@Profile("jpa")
public class MailSmtpNotificationAdapter implements NotificationPort {

    private final MessageChannel smtpOutboundChannel;
    private final MailProperties mailProperties;

    public MailSmtpNotificationAdapter(MessageChannel smtpOutboundChannel, MailProperties mailProperties) {
        this.smtpOutboundChannel = smtpOutboundChannel;
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendSolicitudCreated(String destinatario, String solicitudId, String titulo) {
        String body = "Se registró la solicitud " + solicitudId + ": " + titulo;
        smtpOutboundChannel.send(MessageBuilder.withPayload(body)
                .setHeader(MailHeaders.TO, destinatario)
                .setHeader(MailHeaders.FROM, mailProperties.from())
                .setHeader(MailHeaders.SUBJECT, mailProperties.solicitudCreatedSubject())
                .build());
    }
}
