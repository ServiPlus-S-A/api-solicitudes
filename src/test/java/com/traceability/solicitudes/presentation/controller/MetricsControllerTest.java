package com.traceability.solicitudes.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.traceability.solicitudes.application.service.MetricService;
import com.traceability.solicitudes.presentation.dto.metrics.MetricaResumenResponse;
import com.traceability.solicitudes.presentation.mapper.MetricsDtoMapper;
import com.traceability.solicitudes.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetricService metricService;

    @MockitoBean
    private MetricsDtoMapper mapper;

    @Test
    @WithMockUser(roles = "GERENTE")
    void resumen_ShouldReturnOk() throws Exception {
        when(metricService.obtenerResumen()).thenReturn(null);
        when(mapper.toResponse(any())).thenReturn(mock(MetricaResumenResponse.class));

        mockMvc.perform(get(Constants.API_BASE_PATH + "/metrics/resumen"))
                .andExpect(status().isOk());
    }
}