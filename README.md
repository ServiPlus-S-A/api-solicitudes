# API-SOLICITUDES (Equipo 3 - ServiPlus)

Este repositorio contiene el **Microservicio de Gestión de Solicitudes** de la plataforma **ServiPlus**. El servicio está enfocado exclusivamente en la lógica de backend para la administración del ciclo de vida de las solicitudes de servicio, integrándose mediante llamadas resilientes a otros microservicios y exponiendo endpoints REST protegidos bajo autorización basada en roles (RBAC).

Jhoan Sebastian Fernandez

Andres Perea

Nicolas Mosorongo

---

## 1. Descripción del Proyecto

El microservicio de Gestión de Solicitudes permite a los usuarios registrar, consultar, actualizar y eliminar solicitudes de soporte o servicios técnicos dentro del ecosistema ServiPlus. 

* **Framework principal**: Spring Boot 3.x
* **Lenguaje**: Java 17 / 21
* **Base de Datos**: PostgreSQL (Supabase en producción)
* **Arquitectura**: Clean Architecture con diseño altamente desacoplado y de alta cohesión.

---

## 2. Arquitectura y Tecnologías

El stack tecnológico e infraestructura clave del proyecto comprende:

* **Spring Boot 3 (Web, Security, Actuator, Cache, Mail)**: Núcleo y soporte del framework para inyección de dependencias, seguridad perimetral delegada y caching declarativo.
* **Spring Data JPA**: Abstracción para el mapeo relacional de objetos (ORM) y la persistencia en base de datos.
* **Flyway**: Control de versiones de la base de datos y migraciones automáticas.
* **PostgreSQL / Supabase**: Base de datos relacional para persistir la entidad `SolicitudModel`.
* **Redis**: Caché distribuida de segundo nivel (L2) para optimizar el comportamiento temporal de las lecturas.
* **Caffeine**: Caché local en memoria de primer nivel (L1).
* **Resilience4J**: Implementación nativa del patrón *Circuit Breaker* y límites de tiempo (*Time Limiter*) en integraciones con clientes remotos.
* **Lombok**: Generación automatizada de código redundante (getters, setters, constructores por inyección con `@RequiredArgsConstructor`).
* **JUnit 5 & Mockito**: Suite de pruebas unitarias exhaustivas estructuradas bajo el patrón Given-When-Then.
* **JaCoCo**: Generación de reportes de cobertura de código y validación automatizada de límites mínimos.
* **OpenAPI / Swagger**: Documentación auto-generada del API REST expuesta en `/swagger-ui.html`.

---

## 3. Requisitos Previos

Para ejecutar la aplicación localmente o compilar el proyecto en su entorno, asegúrese de contar con:

* **Java 17 o superior** instalado y configurado en el `PATH` del sistema (el proyecto cuenta con toolchain configurado para Java 21).
* **Docker / Docker Compose** (para levantar las instancias locales de PostgreSQL y Redis de forma automática).
* O bien, accesos de red configurados a las instancias en la nube de **Supabase** y **Redis**.

---

## 4. Variables de Entorno

El microservicio utiliza variables de entorno del sistema para evitar quemar credenciales en texto plano. Configure las siguientes propiedades antes de iniciar el servicio:

| Nombre de Variable | Propósito | Valor de Ejemplo / Local |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | URL de conexión JDBC a la base de datos PostgreSQL | `jdbc:postgresql://localhost:3050/solicitudes_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `solicitudes_user` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `solicitudes_pass` |
| `SPRING_DATA_REDIS_HOST` | Dirección del servidor Redis | `localhost` |
| `SPRING_DATA_REDIS_PORT` | Puerto de escucha del servidor Redis | `6379` |

> [!TIP]
> Puede duplicar el archivo `.env.example` ubicado en la raíz del proyecto, renombrarlo a `.env` y configurar sus variables locales allí.

---

## 5. Instrucciones de Ejecución

El proyecto está configurado con **Gradle** como herramienta de construcción. Ejecute los siguientes comandos desde la terminal en la raíz del proyecto:

### A. Limpiar y compilar el proyecto (Validando estilo de código Checkstyle)
```bash
./gradlew clean compileJava checkstyleMain
```

### B. Ejecutar las pruebas unitarias y generar reporte de cobertura JaCoCo
```bash
./gradlew test
```

### C. Levantar la aplicación localmente
```bash
./gradlew bootRun
```
*La aplicación iniciará y estará disponible en el puerto `8085` bajo el contexto `/api` (ej. `http://localhost:8085/api/swagger-ui.html` para acceder a la documentación interactiva).*

---

## 6. Integración y Pipelines (CI/CD)

Este proyecto está preparado para ejecutarse en pipelines de integración continua (como GitHub Actions). Cada Pull Request o commit en la rama principal ejecuta:
1. Compilación limpia y análisis Checkstyle para validar conformidad estricta del estilo del código.
2. Suite de pruebas JUnit 5 de cobertura.
3. **Verificación de JaCoCo (`jacocoTestCoverageVerification`)**: Se requiere obligatoriamente una cobertura mínima del **80%** de instrucciones cubiertas en el pipeline para permitir el despliegue del artefacto.
