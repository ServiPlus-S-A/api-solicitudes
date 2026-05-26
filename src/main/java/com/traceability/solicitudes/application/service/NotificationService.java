package com.traceability.solicitudes.application.service;

import com.traceability.solicitudes.application.port.NotificationPort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationPort notificationPort;

    public NotificationService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    @Async
    public void notifySolicitudCreated(String destinatario, String solicitudId, String titulo) {
        notificationPort.sendSolicitudCreated(destinatario, solicitudId, titulo);
    }
}
