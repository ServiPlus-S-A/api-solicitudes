package com.traceability.solicitudes.config;

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
import java.util.stream.Collectors;

/**
 * Filtro que procesa la autenticación enviada en la cabecera por el API Gateway.
 */
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderAuthenticationFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String[] parts = token.split("\\.");
                if (parts.length >= 2) {
                    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                    Map<String, Object> claims = objectMapper.readValue(payloadJson, Map.class);
                    
                    Object rolesObj = claims.get("roles");
                    if (rolesObj == null) {
                        rolesObj = claims.get("authorities");
                    }
                    
                    List<String> roles;
                    if (rolesObj instanceof List) {
                        roles = (List<String>) rolesObj;
                    } else if (rolesObj instanceof String) {
                        roles = Collections.singletonList((String) rolesObj);
                    } else {
                        roles = Collections.emptyList();
                    }
                    
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> {
                                String cleanRole = role.toUpperCase();
                                if (!cleanRole.startsWith("ROLE_")) {
                                    cleanRole = "ROLE_" + cleanRole;
                                }
                                return new SimpleGrantedAuthority(cleanRole);
                            })
                            .collect(Collectors.toList());
                    
                    String subject = (String) claims.get("sub");
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            subject, null, authorities
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Usuario '{}' autenticado vía API Gateway con roles: {}", subject, roles);
                }
            } catch (Exception e) {
                log.error("Fallo al autenticar a través del token del Gateway: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
