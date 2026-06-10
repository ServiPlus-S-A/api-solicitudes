package com.traceability.solicitudes.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class JwtRoleConverterTest {

    private final JwtRoleConverter converter = new JwtRoleConverter();

    // Método corregido con el Builder para evitar excepciones de mapas vacíos o nulos
    private Jwt buildJwt(Map<String, Object> claims) {
        var builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user-test"); // Asegura un sujeto por defecto válido

        // Inyectamos los claims dinámicamente sin romper si el mapa viene vacío
        claims.forEach(builder::claim);

        return builder.build();
    }

    @Test
    void shouldConvertRoleList() {
        Jwt jwt = buildJwt(
                Map.of(
                        "roles",
                        List.of("GERENTE", "COORDINADOR")
                )
        );

        var authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_GERENTE", "ROLE_COORDINADOR");
    }

    @Test
    void shouldConvertSingleRoleString() {
        Jwt jwt = buildJwt(
                Map.of(
                        "roles",
                        "GERENTE"
                )
        );

        var authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_GERENTE");
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenNoRoles() {
        // Este era el que fallaba al pasarle un Map.of() vacío
        Jwt jwt = buildJwt(Map.of());

        var authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldNotDuplicateRolePrefix() {
        Jwt jwt = buildJwt(
                Map.of(
                        "roles",
                        List.of("ROLE_ADMIN")
                )
        );

        var authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }
}