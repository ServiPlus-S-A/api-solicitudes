# 📦 api-solicitudes

> Microservicio de gestión de solicitudes desarrollado con **Clean Architecture**, **Spring Boot 3** y buenas prácticas de ingeniería de software.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7.x-red?logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## 📐 Decisiones Arquitectónicas (Clean Architecture)

| Capa / Componente       | Patrón / Estrategia                          | Tecnología / Framework                              |
|-------------------------|----------------------------------------------|-----------------------------------------------------|
| View / Controlador      | Controlador REST + OpenAPI                   | Spring MVC + Springdoc OpenAPI 3                    |
| Seguridad               | Seguridad Perimetral Delegada + RBAC         | Spring Security + `GatewayHeaderAuthenticationFilter` |
| Business Logic          | Use Cases / Service Layer                    | Servicios Spring puros + `@Transactional`           |
| Data Access             | Repository Pattern + ORM                     | Spring Data JPA + Hibernate                         |
| Database Migrations     | Evolución Controlada del Esquema             | Flyway Migrations                                   |
| Caching Layer           | Cache-Aside con TTL Declarativo               | Redis (Spring Cache + `RedisCacheManager`)          |
| Resilience              | Circuit Breaker & Time Limiter               | Resilience4j                                        |
| Integration Testing     | Entornos Aislados Efímeros                   | JUnit 5 + Testcontainers (PostgreSQL / Redis)       |

> [!NOTE]
> **Nota de Diseño de Seguridad:** Siguiendo un modelo de arquitectura *Zero Trust Interno*, la firma digital del JWT es validada exclusivamente por el API Gateway en la frontera. Este microservicio confía en las cabeceras sanitizadas propagadas por el Gateway, abstrayendo la lógica de desencriptado a través de un filtro personalizado de extracción de *claims*.

---

## 🛠️ Documentación

Puedes encontrar la documentación ampliada del proyecto, diagramas de clases, DER y el documento de arquitectura técnica en nuestro repositorio compartido de Google Drive:

📁 [Acceder a Google Drive del Proyecto](https://drive.google.com/drive/u/0/folders/1D96ec5TUwWO3Y1StIQOz0mDU_mWvXsTZ)

---

## 💻 Stack Tecnológico

### Componentes de Software

| Tecnología       | Versión           | Uso                                                          |
|------------------|-------------------|--------------------------------------------------------------|
| Java             | 21 (Amazon Corretto) | Lenguaje de programación principal (Modern Features)      |
| Spring Boot      | 3.4.2             | Framework base del ecosistema backend                        |
| Flyway           | 9.x               | Gestión y control de versiones de bases de datos             |
| Resilience4j     | 2.2.0             | Tolerancia a fallos e integración resiliente                 |
| Lombok           | —                 | Reducción de código boilerplate en DTOs y Entidades          |

### Infraestructura y Persistencia

| Tecnología              | Versión              | Uso                                                          |
|-------------------------|----------------------|--------------------------------------------------------------|
| PostgreSQL (Supabase)   | 15+                  | Base de datos relacional para entornos productivos           |
| Redis Cloud / Local     | 7.x                  | Almacenamiento en caché distribuido de segundo nivel         |
| Docker / Compose        | 2.27+                | Contenedores para empaquetamiento local y productivo         |
| AWS EC2                 | t2.micro / Ubuntu    | Servidor de cómputo en la nube para el despliegue funcional  |
| Testcontainers          | 1.19+                | Orquestación automática de contenedores Docker efímeros para Testing |

---

## 🚀 Requisitos Previos

Antes de compilar o ejecutar el proyecto en tu máquina local, asegúrate de tener:

- ✅ **Docker & Docker Compose** corriendo activamente.
- ✅ **Java Development Kit (JDK) 21** configurado en tus variables de entorno.
- ✅ Un cliente **Git** configurado.

---

## 🏁 Inicio Rápido (Quick Start)

### 1. Clonar el repositorio

```bash
git clone https://github.com/ServiPlus-S-A/api-solicitudes.git
cd api-solicitudes
```

### 2. Configurar Variables de Entorno

Duplica el archivo de ejemplo en la raíz del proyecto para habilitar tus configuraciones locales:

```bash
cp .env.example .env
```

> Asegúrate de que las credenciales del archivo `.env` coincidan con tus puertos locales de Docker o tus instancias activas de Supabase / Redis en la nube.

### 3. Desplegar Infraestructura Local (PostgreSQL y Redis)

Utiliza Docker Compose para levantar las dependencias necesarias de manera automatizada:

```bash
docker compose up -d
```

### 4. Compilar y Ejecutar la Aplicación

Ejecuta el wrapper de Gradle para iniciar el servidor de desarrollo:

```bash
./gradlew bootRun
```

El microservicio se iniciará y estará expuesto en el puerto **8085** bajo el contexto base `/api`.

| Recurso              | URL                                                          |
|----------------------|--------------------------------------------------------------|
| 📖 Swagger UI        | http://localhost:8085/api/swagger-ui/index.html              |
| 📊 Actuator Health   | http://localhost:8085/api/actuator/health                    |

---

## 🔧 Comandos Útiles de Desarrollo

### Control de Calidad y Formato Local

**Compilar y Validar Estilo (Checkstyle):**

```bash
./gradlew clean compileJava checkstyleMain checkstyleTest
```

**Correr Análisis de Código Estático Local:**

> Instala la extensión **SonarLint** en tu IDE para auditar en tiempo real vulnerabilidades y *Code Smells*.

### Ejecución de Pruebas Unitarias e Integración

**Correr Suite de Tests con Reporte de Cobertura:**

```bash
./gradlew test
```

> Las pruebas de integración levantarán automáticamente instancias reales de bases de datos y Redis en contenedores Docker efímeros gracias a **Testcontainers**, garantizando total aislamiento.

**Ver Reporte de Cobertura Local (JaCoCo):**

Abre el archivo generado en tu navegador:

```
build/reports/jacoco/test/html/index.html
```

---

## 📂 Estructura del Repositorio

```
api-solicitudes/
├── .github/workflows/        # Pipelines de Automatización (CI/CD GitHub Actions)
├── config/checkstyle/        # Reglas estrictas de calidad y estilo de código
├── src/
│   ├── main/
│   │   ├── java/com/traceability/solicitudes/
│   │   │   ├── config/       # Configuraciones (Seguridad, Caché, Async)
│   │   │   ├── controller/   # Controladores REST expuestos
│   │   │   ├── integration/  # Clientes externos resiliencia (HTTP Clients)
│   │   │   ├── model/        # Entidades del Modelo de Dominio
│   │   │   ├── repository/   # Repositorios JPA
│   │   │   └── service/      # Capa de Lógica de Negocio Central
│   │   └── resources/
│   │       ├── db/migration/ # Scripts Evolutivos de Base de Datos (Flyway)
│   │       └── application.yml
│   └── test/                 # Pruebas Unitarias e Integración (Testcontainers)
├── docker-compose.yml        # Orquestación de contenedores locales
├── build.gradle              # Configuración de Dependencias Gradle
└── README.md                 # Documentación principal del sistema
```

---

## 📊 Estado del Proyecto

| Sprint | Estado          | Descripción                                                                         |
|--------|-----------------|-------------------------------------------------------------------------------------|
| S0     | ✅ Completado   | Configuración de arquitectura base, Modelos JPA, Migraciones Flyway y Pipeline CI/CD inicial. |
| S1     | ✅ Completado   | Implementación de Lógica de Solicitudes, Filtros de Seguridad y Pruebas Unitarias. |
| S2     | 🏃 En Progreso  | Integración de Disyuntores (Resilience4j), Caché Híbrida L1/L2 y Testcontainers.  |
| S3     | 🔜 Pendiente    | Optimización de infraestructura, auditorías SonarCloud y despliegue final en AWS EC2. |

---

## 🔄 Flujo de Trabajo (Git Flow & Pipeline)

- Las ramas de desarrollo se nombran con el formato `feature/descripcion-corta` o `fix/issue-detectado`.
- Antes de enviar cualquier cambio, se deben pasar las pruebas locales con `./gradlew test`.
- Se crea un **Pull Request (PR)** apuntando hacia la rama `develop`.
- El pipeline de **GitHub Actions** se dispara automáticamente ejecutando:
  - Validación estricta con **Checkstyle**.
  - Pruebas de integración automatizadas con **Testcontainers**.
  - Auditoría de calidad de código con **SonarCloud**.
  - Revisión inteligente y retroalimentación automática de código mediante **Qodo (PR-Agent)**.
  - Verificación de cobertura mínima obligatoria del **80%** con **JaCoCo**.
- Tras la aprobación de los *Quality Gates*, se realiza el merge a `develop`.

---

## 🔐 Secrets de GitHub Actions

Para el correcto funcionamiento de los workflows de automatización, configure las siguientes variables en `Settings -> Secrets and variables -> Actions`:

| Variable / Secret      | Propósito                                                                     |
|------------------------|-------------------------------------------------------------------------------|
| `SONAR_TOKEN`          | Token de autenticación privado para reportar métricas a SonarCloud.           |
| `OPENAI_API_KEY`       | Llave API para energizar las revisiones automáticas del agente de IA Qodo.    |
| `DOCKERHUB_USERNAME`   | Usuario de DockerHub para almacenar imágenes construidas del microservicio.   |
| `DOCKERHUB_TOKEN`      | Token seguro de acceso con alcance de escritura para DockerHub.               |
| `EC2_HOST`             | Dirección IP pública de la instancia AWS EC2 de producción.                   |
| `EC2_USER`             | Usuario del sistema operativo para acceso SSH (ej. `ubuntu`).                 |
| `EC2_SSH_KEY`          | Llave privada `.pem` completa para la conexión segura con el servidor.        |

---

## 👥 Créditos

Desarrollado por el equipo de **Ingeniería de Sistemas** de la **Universidad del Valle** (Sede Cali), Escuela de Ingeniería de Sistemas y Computación:

- **Jhoan Sebastian Fernandez** — [GitHub Profile](https://github.com)
- **Andres Perea** — [GitHub Profile](https://github.com)
- **Nicolas Mosorongo** — [GitHub Profile](https://github.com)

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT** — consulte el archivo [`LICENSE`](./LICENSE) para obtener más información.
