package com.traceability.solicitudes.application.service;

import static org.mockito.Mockito.verify;

import com.traceability.solicitudes.application.port.NotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationPort notificationPort;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationPort);
    }

    @Test
    void should_delegate_notification() {
        notificationService.notifySolicitudCreated("user@test.com", "id-1", "Titulo");

        verify(notificationPort).sendSolicitudCreated("user@test.com", "id-1", "Titulo");
    }
}
