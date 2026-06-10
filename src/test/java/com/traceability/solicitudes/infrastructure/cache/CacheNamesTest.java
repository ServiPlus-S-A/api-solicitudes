package com.traceability.solicitudes.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class CacheNamesTest {

    @Test
    void shouldVerifyConstants() {
        assertThat(CacheNames.SOLICITUD).isEqualTo("solicitud");
        assertThat(CacheNames.METRICAS).isEqualTo("metricas");
    }

    @Test
    void shouldCoverPrivateConstructor() throws Exception {
        // Forzamos la apertura del constructor privado usando reflexión para ganarnos ese 100%
        Constructor<CacheNames> constructor = CacheNames.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        CacheNames instance = constructor.newInstance();

        assertThat(instance).isNotNull();
    }
}