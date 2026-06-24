package com.traceability.solicitudes.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Filtro que procesa la autenticación enviada en la cabecera por el API Gateway.
 */
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderAuthenticationFilter.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            procesarAutenticacion(authHeader.substring(7));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae la lógica pesada a un método privado para reducir drásticamente la Complejidad Cognitiva (java:S3776).
     */
    private void procesarAutenticacion(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> claims = OBJECT_MAPPER.readValue(
                    payloadJson, new TypeReference<Map<String, Object>>() {}
            );

            Object rolesObj = claims.get("roles") != null ? claims.get("roles") : claims.get("authorities");

            // Reemplazamos la cadena de if/else por una expresión Switch con Pattern Matching
            List<String> roles = switch (rolesObj) {
                case List<?> list -> list.stream().map(Object::toString).toList();
                case String roleStr -> Collections.singletonList(roleStr);
                default -> Collections.emptyList();
            };

            // Cambiamos .collect(Collectors.toList()) por .toList() que es inmutable y limpio
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> {
                        String cleanRole = role.toUpperCase();
                        if (!cleanRole.startsWith(ROLE_PREFIX)) {
                            cleanRole = ROLE_PREFIX + cleanRole;
                        }
                        return new SimpleGrantedAuthority(cleanRole);
                    })
                    .toList();

            String subject = (String) claims.get("sub");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    subject, null, authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("Usuario '{}' autenticado vía API Gateway con roles: {}", subject, roles);

        } catch (IOException | IllegalArgumentException e) {
            log.error("Fallo al autenticar a través del token del Gateway", e);
            SecurityContextHolder.clearContext();
        }
    }
}