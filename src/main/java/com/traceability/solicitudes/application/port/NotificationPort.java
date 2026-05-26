package com.traceability.solicitudes.application.port;

public interface NotificationPort {

    void sendSolicitudCreated(String destinatario, String solicitudId, String titulo);
}
