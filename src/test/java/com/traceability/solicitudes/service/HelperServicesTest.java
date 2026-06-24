package com.traceability.solicitudes.service;

import com.traceability.solicitudes.dto.SolicitudMapper;
import com.traceability.solicitudes.dto.SolicitudRequestDTO;
import com.traceability.solicitudes.dto.SolicitudResponseDTO;
import com.traceability.solicitudes.integration.ClienteClient;
import com.traceability.solicitudes.integration.ServicioClient;
import com.traceability.solicitudes.model.SolicitudModel;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Pruebas unitarias adicionales para los servicios auxiliares, clientes de integración y mapeadores.
 */
@ExtendWith(MockitoExtension.class)
class HelperServicesTest { // 👈 Corregido: Se quitó el modificador 'public'

    @Test
    void testMetricService() {
        MeterRegistry registry = Mockito.mock(MeterRegistry.class);
        Counter counter = Mockito.mock(Counter.class);
        Mockito.when(registry.counter(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(counter);

        MetricService metricService = new MetricService(registry);
        metricService.incrementarContador("test.metric");

        Mockito.verify(counter, Mockito.times(1)).increment();
    }

    @Test
    void testNotificationServiceWithMailSender() {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        NotificationService notificationService = new NotificationService(mailSender);
        notificationService.enviarNotificacion("test@dest.com", "Subject", "Body");

        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void testNotificationServiceWithoutMailSender() {
        NotificationService notificationService = new NotificationService(null);

        // 👈 Corregido: Se añade aserción explícita para asegurar que no arroje excepciones (java:S2699)
        Assertions.assertDoesNotThrow(() ->
                notificationService.enviarNotificacion("test@dest.com", "Subject", "Body")
        );
    }

    @Test
    void testClienteClientAndFallback() {
        ClienteClient client = new ClienteClient();
        String res = client.obtenerCliente(1L);
        Assertions.assertEquals("Cliente info ID: 1", res);

        String fallbackRes = client.obtenerClienteFallback(1L, new RuntimeException("Error"));
        Assertions.assertTrue(fallbackRes.contains("Fallback"));
    }

    @Test
    void testServicioClientAndFallback() {
        ServicioClient client = new ServicioClient();
        String res = client.obtenerServicio(2L);
        Assertions.assertEquals("Servicio info ID: 2", res);

        String fallbackRes = client.obtenerServicioFallback(2L, new RuntimeException("Error"));
        Assertions.assertTrue(fallbackRes.contains("Fallback"));
    }

    @Test
    void testSolicitudMapper() {
        SolicitudMapper mapper = new SolicitudMapper();

        SolicitudRequestDTO request = SolicitudRequestDTO.builder()
                .idCliente(1L)
                .idTipoServicio(2L)
                .asunto("Asunto")
                .descripcion("Desc")
                .estado("Pendiente")
                .build();

        SolicitudModel model = mapper.toEntity(request);
        Assertions.assertEquals(1L, model.getIdCliente());

        SolicitudResponseDTO response = mapper.toResponse(model);
        Assertions.assertEquals(1L, response.getIdCliente());

        Assertions.assertNull(mapper.toEntity(null));
        Assertions.assertNull(mapper.toResponse(null));
    }
}