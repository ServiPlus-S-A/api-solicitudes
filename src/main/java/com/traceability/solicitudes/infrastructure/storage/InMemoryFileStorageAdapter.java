package com.traceability.solicitudes.infrastructure.storage;

import com.traceability.solicitudes.application.port.FileStoragePort;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("inmemory")
public class InMemoryFileStorageAdapter implements FileStoragePort {

    private final Map<String, byte[]> bucket = new ConcurrentHashMap<>();

    @Override
    public String store(String objectKey, byte[] content, String contentType) {
        bucket.put(objectKey, content);
        return objectKey;
    }

    @Override
    public byte[] retrieve(String storageKey) {
        byte[] content = bucket.get(storageKey);
        if (content == null) {
            throw new IllegalArgumentException("Archivo no encontrado en storage: " + storageKey);
        }
        return content;
    }
}
