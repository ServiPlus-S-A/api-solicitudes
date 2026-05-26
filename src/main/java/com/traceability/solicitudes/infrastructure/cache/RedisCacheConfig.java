package com.traceability.solicitudes.infrastructure.cache;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
@Profile("jpa")
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${app.cache.solicitud-ttl-ms}") long solicitudTtlMs,
            @Value("${app.cache.metricas-ttl-ms}") long metricasTtlMs) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration(
                        CacheNames.SOLICITUD,
                        defaultConfig.entryTtl(Duration.ofMillis(solicitudTtlMs)))
                .withCacheConfiguration(
                        CacheNames.METRICAS,
                        defaultConfig.entryTtl(Duration.ofMillis(metricasTtlMs)))
                .build();
    }
}
