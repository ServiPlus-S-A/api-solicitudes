package com.traceability.solicitudes.application.port;

public interface FileStoragePort {

    String store(String objectKey, byte[] content, String contentType);

    byte[] retrieve(String storageKey);
}
