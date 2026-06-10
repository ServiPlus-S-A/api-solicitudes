package com.traceability.solicitudes.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

class InMemoryCacheConfigTest {

    private final InMemoryCacheConfig config = new InMemoryCacheConfig();

    @Test
    void shouldCreateInMemoryCacheManager() {
        CacheManager cacheManager = config.cacheManager();

        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).containsExactlyInAnyOrder("solicitud", "metricas");
    }
}