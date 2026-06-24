package com.traceability.solicitudes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad unificada para el microservicio de Solicitudes.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * CONFIGURACIÓN PARA DESARROLLO LOCAL (Perfil: dev)
     * Desactiva el login y permite probar libremente en Swagger.
     */
    @Bean
    @Profile("dev")
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Abre las puertas en tu PC
                );
        return http.build();
    }

    /**
     * CONFIGURACIÓN PARA PRODUCCIÓN (Perfiles que NO sean dev)
     * Protege la API y exige el filtro del GatewayHeader.
     */
    @Bean
    @Profile("!dev")
    public SecurityFilterChain securityFilterChainProd(HttpSecurity http) throws Exception {
        http
                // NOSONAR: CSRF se deshabilita porque el microservicio es Stateless
                .csrf(csrf -> csrf.disable()) // NOSONAR
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new GatewayHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}