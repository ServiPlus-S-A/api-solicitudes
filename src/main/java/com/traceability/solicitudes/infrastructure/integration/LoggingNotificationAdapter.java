package com.traceability.solicitudes.infrastructure.integration;

import com.traceability.solicitudes.application.port.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("inmemory")
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void sendSolicitudCreated(String destinatario, String solicitudId, String titulo) {
        log.info("Notificación (dev) a {} — solicitud {}: {}", destinatario, solicitudId, titulo);
    }
}
