# TraceAbility — Microservicio Solicitudes (Backend)

API REST en capas con PostgreSQL, Redis (caché L1 Caffeine + L2 Redis), MinIO (S3), SMTP, Kong Gateway y JWT RS256.

## Stack de infraestructura

| Componente | Tecnología | Perfil |
|------------|------------|--------|
| API Gateway | Kong 3.9 (CORS, JWT, rate limit, correlation-id) | Docker |
| Base de datos | PostgreSQL 16 + Flyway | `jpa` |
| Caché | Caffeine (L1) + Redis (L2) | `jpa` |
| Archivos | MinIO (S3 API) | `jpa` |
| Notificaciones | Spring Integration + SMTP → MailHog | `jpa` |
| Seguridad | Spring Security OAuth2 Resource Server | `jpa` |
| Dev/tests sin Docker | InMemory repos + logging | `inmemory` |

## Prerequisites

- Docker 24+ y Docker Compose v2.29+
- Java 21 (Temurin) para tests locales
- Python 3 + `PyJWT` para generar tokens de prueba (`pip install PyJWT`)

## Quick start (stack completo)

```bash
cp .env.example .env
# Editar contraseñas en .env

chmod +x scripts/generate-dev-keys.sh scripts/generate-dev-jwt.sh
chmod +x docker/kong/docker-entrypoint-kong.sh

./scripts/generate-dev-keys.sh
docker compose up --build
```

### URLs

| Servicio | URL |
|----------|-----|
| API (Kong) | http://localhost:8000/api/v1/solicitudes |
| MailHog UI | http://localhost:8025 |
| Actuator | http://localhost:8080/actuator/health (red interna) |

### Probar con JWT

```bash
export TOKEN=$(./scripts/generate-dev-jwt.sh --roles COORDINADOR)
curl -s -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Demo","descripcion":"Prueba E2E","solicitanteId":"user-1"}' \
  http://localhost:8000/api/v1/solicitudes
```

El claim `iss` del token debe coincidir con `JWT_KONG_ISSUER` (por defecto `traceability-dev`).

## Tests locales

```bash
./gradlew test check
open build/reports/jacoco/test/html/index.html
```

Perfil `test,inmemory` — sin PostgreSQL, Redis ni Docker. JaCoCo exige 100% de cobertura de línea en `domain` y `application`.

## CI/CD (GitHub Actions)

El workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) se ejecuta en `push` y `pull_request` hacia `main` y `develop`:

1. **Gradle** — `./gradlew check` (tests unitarios + verificación JaCoCo al 100% en `domain` y `application`).
2. **SonarQube Cloud** — `./gradlew sonar` (análisis estático, cobertura JaCoCo y quality gate).

### Secretos requeridos en GitHub

| Secreto | Uso |
|---------|-----|
| `SONAR_TOKEN` | Token de SonarCloud con permiso *Execute analysis* ([generar aquí](https://sonarcloud.io/account/security)) |

Proyecto SonarCloud: `ServiPlus-S-A_api-solicitudes` (organización `serviplus-s-a`). El análisis en PRs de forks se omite si no hay token disponible en el fork.

Análisis local con Sonar (opcional):

```bash
export SONAR_TOKEN=<tu-token>
./gradlew check sonar
```

## Perfiles Spring

| Perfil | Uso |
|--------|-----|
| `inmemory` | `./gradlew test`, dev ligero |
| `jpa` | `docker compose` |

## Estructura `infrastructure/`

```
infrastructure/
├── cache/          # Redis, Caffeine, composite
├── config/         # Properties (storage, mail)
├── integration/    # SMTP (Spring Integration), audit
├── persistence/    # JPA + inmemory
├── security/       # JWT RS256 + RBAC
├── storage/        # S3/MinIO + inmemory
└── web/            # Async, exception handler
```

## ADRs aplicados

- **ADR-001:** Redis + Caffeine multinivel, TTL solicitud 5 min / métricas 1 min
- **ADR-006:** JWT RS256; validación en Kong y microservicio

## Consistencia entre repositorios

Ver **[HANDOFF.md](./HANDOFF.md)** — contratos API, JWT, RBAC, convenciones de entorno y checklist para el repo frontend y futuros microservicios.

## Frontend

El cliente Next.js vive en un **repositorio separado** (no incluido aquí). Debe seguir `HANDOFF.md`.
