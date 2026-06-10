package com.traceability.solicitudes.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traceability.solicitudes.application.service.SolicitudService;
import com.traceability.solicitudes.presentation.dto.solicitud.CreateSolicitudRequest;
import com.traceability.solicitudes.presentation.dto.solicitud.SolicitudResponse;
import com.traceability.solicitudes.presentation.dto.solicitud.UpdateEstadoRequest;
import com.traceability.solicitudes.presentation.mapper.SolicitudDtoMapper;
import com.traceability.solicitudes.utils.Constants;
import com.traceability.solicitudes.domain.model.SolicitudEstado; // Asegúrate de que esta ruta a tu enum sea correcta
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SolicitudService solicitudService;

    @MockitoBean
    private SolicitudDtoMapper mapper;

    private final String basePath = Constants.API_BASE_PATH + "/solicitudes";

    @Test
    @WithMockUser(roles = "SOLICITANTE")
    void createSolicitud_ShouldReturnCreated() throws Exception {
        // Usamos el constructor/Record real para que pasen los validadores de Jakarta
        CreateSolicitudRequest request = new CreateSolicitudRequest("Soporte Técnico", "Descripción de la solicitud", "USER-123");
        when(mapper.toCommand(any())).thenReturn(null);
        when(solicitudService.create(any(), any())).thenReturn(null);
        when(mapper.toResponse(any())).thenReturn(mock(SolicitudResponse.class));

        mockMvc.perform(post(basePath)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "SOLICITANTE")
    void getById_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        when(solicitudService.getById(id)).thenReturn(null);
        when(mapper.toResponse(any())).thenReturn(mock(SolicitudResponse.class));

        mockMvc.perform(get(basePath + "/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "COORDINADOR")
    void listSolicitudes_ShouldReturnOk() throws Exception {
        when(solicitudService.list(anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get(basePath)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void updateEstado_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        // Truco dinámico: Tomamos el primer estado que exista en tu enum, se llame como se llame
        SolicitudEstado estadoCualquiera = SolicitudEstado.values()[0];
        UpdateEstadoRequest request = new UpdateEstadoRequest(estadoCualquiera);

        when(solicitudService.updateEstado(eq(id), any(), any())).thenReturn(null);
        when(mapper.toResponse(any())).thenReturn(mock(SolicitudResponse.class));

        mockMvc.perform(put(basePath + "/{id}/estado", id)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }}