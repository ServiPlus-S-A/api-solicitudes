package com.traceability.solicitudes.infrastructure.storage;

import com.traceability.solicitudes.application.port.FileStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@Profile("jpa")
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;
    private final String bucket;

    public S3FileStorageAdapter(S3Client s3Client, com.traceability.solicitudes.infrastructure.config.StorageProperties properties) {
        this.s3Client = s3Client;
        this.bucket = properties.bucket();
    }

    @Override
    public String store(String objectKey, byte[] content, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(content));
        return objectKey;
    }

    @Override
    public byte[] retrieve(String storageKey) {
        try {
            return s3Client
                    .getObject(GetObjectRequest.builder().bucket(bucket).key(storageKey).build())
                    .readAllBytes();
        } catch (NoSuchKeyException ex) {
            throw new IllegalArgumentException("Archivo no encontrado en storage: " + storageKey, ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Error leyendo archivo del bucket", ex);
        }
    }
}
