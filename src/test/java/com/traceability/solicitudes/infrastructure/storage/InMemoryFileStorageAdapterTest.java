package com.traceability.solicitudes.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class InMemoryFileStorageAdapterTest {

    private final InMemoryFileStorageAdapter storage =
            new InMemoryFileStorageAdapter();

    @Test
    void shouldStoreAndRetrieveFile() {

        byte[] content = "hola".getBytes();

        String key =
                storage.store(
                        "archivo.txt",
                        content,
                        "text/plain");

        assertThat(storage.retrieve(key))
                .isEqualTo(content);
    }

    @Test
    void shouldThrowWhenFileDoesNotExist() {

        assertThatThrownBy(() -> storage.retrieve("inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Archivo no encontrado");
    }
}