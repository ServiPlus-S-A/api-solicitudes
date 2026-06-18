package com.traceability.solicitudes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio asíncrono para enviar notificaciones de correo.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    /**
     * Constructor con JavaMailSender opcional.
     * @param mailSender cliente mail sender de Spring
     */
    public NotificationService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía una notificación por email de forma asíncrona.
     * @param destinatario correo del destinatario
     * @param asunto asunto del correo
     * @param mensaje cuerpo del mensaje
     */
    @Async("taskExecutor")
    public void enviarNotificacion(String destinatario, String asunto, String mensaje) {
        log.info("Iniciando envío asíncrono de correo para: {}", destinatario);
        if (mailSender != null) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(destinatario);
                mailMessage.setSubject(asunto);
                mailMessage.setText(mensaje);
                mailSender.send(mailMessage);
                log.info("Correo enviado exitosamente a: {}", destinatario);
            } catch (Exception e) {
                log.error("Error al enviar correo: {}", e.getMessage());
            }
        } else {
            log.info("[MOCK MAIL] Para: {}, Asunto: {}, Mensaje: {}", destinatario, asunto, mensaje);
        }
    }
}
