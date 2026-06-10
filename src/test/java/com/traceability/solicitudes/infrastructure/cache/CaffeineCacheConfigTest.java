package com.traceability.solicitudes.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

class CaffeineCacheConfigTest {

    private final CaffeineCacheConfig config = new CaffeineCacheConfig();

    @Test
    void shouldCreateCompositeCacheManager() {
        RedisCacheManager mockRedisCacheManager = mock(RedisCacheManager.class);

        CacheManager result = config.compositeCacheManager(mockRedisCacheManager, 60000L, 30000L);

        assertThat(result).isNotNull();
    }
}