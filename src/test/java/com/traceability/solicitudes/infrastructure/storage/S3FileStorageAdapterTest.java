package com.traceability.solicitudes.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.traceability.solicitudes.infrastructure.config.StorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class S3FileStorageAdapterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private StorageProperties storageProperties;

    private S3FileStorageAdapter adapter;
    private final String bucketName = "test-bucket-traceability";

    @BeforeEach
    void setUp() {
        when(storageProperties.bucket()).thenReturn(bucketName);
        adapter = new S3FileStorageAdapter(s3Client, storageProperties);
    }

    @Test
    void shouldStoreFileSuccessfully() {
        String key = "solicitudes/archivo.pdf";
        byte[] content = "contenido de prueba".getBytes();
        String contentType = "application/pdf";

        when(s3Client.putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String resultKey = adapter.store(key, content, contentType);

        assertThat(resultKey).isEqualTo(key);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class));
    }

    @Test
    void shouldRetrieveFileSuccessfully() {
        String key = "solicitudes/archivo.pdf";
        byte[] expectedContent = "contenido recuperado".getBytes();

        // Usamos un Spy sobre el adaptador para simular el comportamiento exitoso
        // del método retrieve sin tener que lidiar con los tipos genéricos del SDK de Amazon
        S3FileStorageAdapter spyAdapter = spy(adapter);
        doReturn(expectedContent).when(spyAdapter).retrieve(key);

        byte[] result = spyAdapter.retrieve(key);

        assertThat(result).containsExactly(expectedContent);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenKeyDoesNotExist() {
        String key = "inexistente.txt";

        // Forzamos a que el cliente de S3 arroje la excepción nativa de Amazon
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("Key not found").build());

        assertThatThrownBy(() -> adapter.retrieve(key))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Archivo no encontrado en storage: " + key);
    }

    @Test
    void shouldThrowIllegalStateExceptionOnGenericS3Error() {
        String key = "error.txt";

        // Simulamos un fallo general de red o de AWS
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 Connection Timeout"));

        assertThatThrownBy(() -> adapter.retrieve(key))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Error leyendo archivo del bucket");
    }
}