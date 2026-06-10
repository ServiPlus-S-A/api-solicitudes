package com.traceability.solicitudes.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.traceability.solicitudes.application.service.FileService;
import com.traceability.solicitudes.presentation.dto.file.ArchivoResponse;
import com.traceability.solicitudes.presentation.mapper.FileDtoMapper;
import com.traceability.solicitudes.utils.Constants;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private FileDtoMapper mapper;

    private final String basePath = Constants.API_BASE_PATH + "/files";

    @Test
    @WithMockUser(roles = "SOLICITANTE")
    void uploadFile_ShouldReturnOk() throws Exception {
        UUID solicitudId = UUID.randomUUID();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "contenido de prueba".getBytes()
        );

        when(fileService.upload(any(), any(), any(), any())).thenReturn(null);
        when(mapper.toResponse(any())).thenReturn(mock(ArchivoResponse.class));

        mockMvc.perform(multipart(basePath + "/solicitudes/{solicitudId}", solicitudId)
                        .file(mockFile)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "SOLICITANTE")
    void listFiles_ShouldReturnOk() throws Exception {
        UUID solicitudId = UUID.randomUUID();
        when(fileService.listBySolicitud(solicitudId)).thenReturn(List.of());

        mockMvc.perform(get(basePath + "/solicitudes/{solicitudId}", solicitudId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void downloadFile_ShouldReturnOk() throws Exception {
        UUID archivoId = UUID.randomUUID();
        when(fileService.download(archivoId)).thenReturn(new byte[0]);

        mockMvc.perform(get(basePath + "/{archivoId}/download", archivoId))
                .andExpect(status().isOk());
    }
}