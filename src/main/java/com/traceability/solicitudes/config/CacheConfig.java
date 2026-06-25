package com.traceability.solicitudes.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuración nativa de caché declarativo de Spring usando Redis.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Define el CacheManager nativo de Spring usando Redis.
     * @param connectionFactory fábrica de conexiones Redis
     * @return gestor de caché
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 1. Creamos un ObjectMapper personalizado para que Jackson entienda
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Corrección de seguridad: Restringir subtipos permitidos solo a nuestro paquete base
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType("com.traceability.solicitudes")
                .build();

        // Esto le permite a Redis guardar el tipo de clase de forma segura
        objectMapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 2. Le pasamos el mapper configurado al serializador de Jackson
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)) // TTL de 5 minutos
                .disableCachingNullValues()
                // Serializa las llaves como strings limpios
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new
                        StringRedisSerializer()))
                // Serializa los objetos usando nuestro Jackson supercargado
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        jsonSerializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}