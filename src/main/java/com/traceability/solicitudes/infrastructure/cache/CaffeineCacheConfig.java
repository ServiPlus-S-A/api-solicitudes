package com.traceability.solicitudes.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("jpa")
public class CaffeineCacheConfig {

    @Bean
    @Primary
    public CacheManager compositeCacheManager(
            RedisCacheManager redisCacheManager,
            @Value("${app.cache.solicitud-ttl-ms}") long solicitudTtlMs,
            @Value("${app.cache.metricas-ttl-ms}") long metricasTtlMs) {
        CaffeineCacheManager caffeine = new CaffeineCacheManager(CacheNames.SOLICITUD, CacheNames.METRICAS);
        caffeine.registerCustomCache(
                CacheNames.SOLICITUD,
                Caffeine.newBuilder().maximumSize(500).expireAfterWrite(Duration.ofMillis(solicitudTtlMs)).build());
        caffeine.registerCustomCache(
                CacheNames.METRICAS,
                Caffeine.newBuilder().maximumSize(50).expireAfterWrite(Duration.ofMillis(metricasTtlMs)).build());

        CompositeCacheManager composite = new CompositeCacheManager();
        composite.setCacheManagers(List.of(caffeine, redisCacheManager));
        composite.setFallbackToNoOpCache(false);
        return composite;
    }
}
