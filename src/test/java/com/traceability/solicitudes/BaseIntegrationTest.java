package com.traceability.solicitudes;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

// @SpringBootTest levanta el contexto completo para los tests de integración
// RANDOM_PORT asigna un puerto aleatorio disponible al servidor HTTP
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    // Definimos el contenedor como constante estática única (Sin @Container)
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine") // Actualizado a la de la guía
                    .withDatabaseName("serviplus_test_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    static {
        // El truco definitivo: Se ejecuta UNA sola vez para todo el proyecto.
        // El contenedor no se apagará hasta que terminen absolutamente todos los tests.
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        // Redirige dinámicamente las conexiones de Spring al contenedor único
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Flyway también necesita la misma URL para aplicar tus V1__, V2__ sin perderse
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }
}