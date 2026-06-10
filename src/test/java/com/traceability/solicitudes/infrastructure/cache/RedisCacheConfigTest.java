package com.traceability.solicitudes.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

class RedisCacheConfigTest {

    private final RedisCacheConfig config = new RedisCacheConfig();

    @Test
    void shouldCreateRedisCacheManager() {
        RedisConnectionFactory mockConnectionFactory = mock(RedisConnectionFactory.class);

        RedisCacheManager result = config.redisCacheManager(mockConnectionFactory, 60000L, 30000L);

        assertThat(result).isNotNull();
    }
}