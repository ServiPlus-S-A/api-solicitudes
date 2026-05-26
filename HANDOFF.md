# TraceAbility — Handoff de consistencia entre repositorios

| Campo | Valor |
|-------|--------|
| **Versión** | 1.0 |
| **Fecha** | 2026-05-26 |
| **Ámbito** | Plataforma TraceAbility — alineación backend / frontend / gateway |
| **Fuente de verdad arquitectónica** | DAS TraceAbility v2.0 (20 mayo 2026) |
| **Repo de referencia (implementado)** | `traceability-solicitudes-service` (este repositorio) |
| **Repo pendiente** | `traceability-frontend` (Next.js — repositorio separado) |

---

## 1. Propósito de este documento

Este handoff define **decisiones, contratos y convenciones obligatorias** para que todos los repositorios de TraceAbility permanezcan alineados. Cualquier desviación debe:

1. Estar documentada como ADR nuevo o actualización del DAS.
2. Reflejarse en este archivo (nueva versión).
3. No introducirse silenciosamente en un solo repo.

**Regla de oro:** Si el frontend y el backend interpretan distinto un JWT, un rol o una ruta, el bug es de **gobernanza**, no de código aislado.

---

## 2. Mapa de repositorios y responsabilidades

| Repositorio | Bounded context | Stack | Estado |
|-------------|-----------------|-------|--------|
| `traceability-solicitudes-service` | **Solicitudes** | Java 21, Spring Boot 3.5.11, PostgreSQL, Redis, MinIO, Kong | Implementado |
| `traceability-frontend` | UI plataforma (Solicitudes, Reportes, Dashboard) | Next.js, Zustand, Axios, SWR | Por crear |
| *(futuro)* `traceability-parametrizacion` | Parametrización | TBD | Solo en diagrama Kong |
| *(futuro)* `traceability-finanzas` | Finanzas | TBD | Solo en diagrama Kong |

### Comunicación entre repos

```
[Browser / Next.js]  →  [Kong Gateway]  →  [Microservicio Solicitudes]
        ↑                      ↑                      ↑
   traceability-frontend   plugins JWT/CORS    traceability-solicitudes-service
```

- **Nunca** llamar al microservicio directamente desde el navegador en producción; siempre vía **Kong**.
- En desarrollo local: base URL del frontend = `http://localhost:{KONG_PROXY_PORT}` (default **8000**).

---

## 3. Decisiones arquitectónicas (catálogo obligatorio)

### 3.1 ADRs formales (DAS)

| ID | Decisión | Implementación actual | Repos afectados |
|----|----------|----------------------|-----------------|
| **ADR-001** | Caché distribuida Redis + Caffeine L1 opcional | Redis L2 + Caffeine L1 (`jpa` profile) | Backend |
| **ADR-006** | Autenticación JWT RS256; auth primaria en Gateway; RBAC en microservicio | Kong `jwt` + Spring `@PreAuthorize` | Backend, Frontend, Kong |

### 3.2 Decisiones de plataforma (DAS + diagramas UML)

| Tema | Decisión | Consistencia requerida |
|------|----------|------------------------|
| Estilo backend | Capas: Presentación → Aplicación → Dominio → Infraestructura | Frontend usa capas análogas (ver §8) |
| API | REST + JSON | `Content-Type: application/json` salvo uploads |
| Versionado API | Prefijo `/api/v1` | **Obligatorio** en todos los servicios nuevos |
| Paginación | Default **10** ítems por página | Query: `page` (0-based), `size` (default 10) |
| Seguridad | RBAC por roles | Ver matriz §6 |
| Errores HTTP | Semánticos + cuerpo estructurado | RFC 7807 `ProblemDetail` en backend |
| Async / notificaciones | `@Async` + cola SMTP | No bloqueante para el cliente |
| Trazabilidad | Header `X-Correlation-Id` (Kong) | Frontend debe propagar si genera requests propias |
| Resiliencia | resilience4j circuit breaker (storage externo) | Frontend: timeouts + retry limitado en Axios |

### 3.3 Versiones pinneadas (no usar `latest`)

| Componente | Versión |
|------------|---------|
| Java | 21 (Temurin) |
| Spring Boot | 3.5.11 |
| PostgreSQL | 16.8-alpine3.20 |
| Redis | 7.4.9-alpine3.21 |
| Kong | 3.9.1 |
| MinIO | RELEASE.2024-12-18T13-15-44Z |
| MailHog (dev) | v1.0.1 |

---

## 4. Contrato de autenticación (JWT) — CRÍTICO para frontend

### 4.1 Flujo

1. El cliente obtiene un JWT (en producción: servicio de identidad / Gateway; en dev: script local).
2. Cada request lleva: `Authorization: Bearer <token>`.
3. **Kong** valida firma RS256 y `exp`.
4. El **microservicio** vuelve a validar firma con clave pública y aplica **RBAC**.

### 4.2 Claims obligatorios

| Claim | Tipo | Uso |
|-------|------|-----|
| `iss` | string | Debe coincidir con `JWT_KONG_ISSUER` (default: `traceability-dev`) |
| `sub` | string | ID de usuario (auditoría en backend) |
| `exp` | number | Expiración (Unix timestamp) |
| `roles` | string[] **o** string | Roles RBAC (ver §6) |

### 4.3 Mapeo de roles en backend

El backend antepone `ROLE_` si no viene en el claim:

- Token: `"roles": ["COORDINADOR"]` → Spring: `ROLE_COORDINADOR`
- Token: `"roles": "COORDINADOR"` → también soportado

### 4.4 Generación de token en desarrollo

```bash
# En traceability-solicitudes-service
./scripts/generate-dev-keys.sh          # una vez
pip install PyJWT                        # una vez
export TOKEN=$(./scripts/generate-dev-jwt.sh --roles COORDINADOR,SOLICITANTE)
```

**Frontend debe usar el mismo `iss` y el mismo par de claves de desarrollo** (solo `public.pem` en el cliente si valida localmente; normalmente no valida, solo envía el token).

### 4.5 Variables de entorno alineadas

| Variable | Backend | Frontend (sugerido) |
|----------|---------|---------------------|
| `JWT_KONG_ISSUER` | Sí | `NEXT_PUBLIC_JWT_ISSUER` o documentar que no aplica |
| `JWT_PUBLIC_KEY_LOCATION` | Sí (Resource Server) | No (salvo BFF) |
| Base API | N/A | `NEXT_PUBLIC_API_BASE_URL=http://localhost:8000` |

---

## 5. Contrato API — Microservicio Solicitudes

**Base URL (dev):** `http://localhost:8000`  
**Prefijo:** `/api/v1`  
**OpenAPI:** `http://localhost:8080/swagger-ui.html` (directo al servicio, red Docker) o vía actuator interno

### 5.1 Endpoints

| Método | Ruta | Roles | Descripción |
|--------|------|-------|-------------|
| `POST` | `/api/v1/solicitudes` | COORDINADOR, GERENTE, SOLICITANTE | Crear solicitud |
| `GET` | `/api/v1/solicitudes/{id}` | COORDINADOR, GERENTE, SOLICITANTE | Obtener por ID |
| `GET` | `/api/v1/solicitudes?page=&size=` | COORDINADOR, GERENTE | Listado paginado |
| `PUT` | `/api/v1/solicitudes/{id}/estado` | COORDINADOR, GERENTE | Cambiar estado |
| `GET` | `/api/v1/metrics/resumen` | COORDINADOR, GERENTE | Métricas agregadas |
| `POST` | `/api/v1/files/solicitudes/{solicitudId}` | COORDINADOR, GERENTE, SOLICITANTE | Upload (`multipart/form-data`, field `file`) |
| `GET` | `/api/v1/files/solicitudes/{solicitudId}` | COORDINADOR, GERENTE, SOLICITANTE | Listar archivos |
| `GET` | `/api/v1/files/{archivoId}/download` | COORDINADOR, GERENTE | Descargar binario |

### 5.2 Modelos JSON (nombres exactos — camelCase)

**Crear solicitud** `POST /api/v1/solicitudes`

```json
{
  "titulo": "string",
  "descripcion": "string",
  "solicitanteId": "string"
}
```

**Respuesta solicitud**

```json
{
  "id": "uuid",
  "titulo": "string",
  "descripcion": "string",
  "solicitanteId": "string",
  "estado": "BORRADOR | ENVIADA | EN_REVISION | APROBADA | RECHAZADA",
  "creadoEn": "2026-05-26T12:00:00Z",
  "actualizadoEn": "2026-05-26T12:00:00Z"
}
```

**Actualizar estado** `PUT /api/v1/solicitudes/{id}/estado`

```json
{
  "estado": "EN_REVISION"
}
```

**Métricas** `GET /api/v1/metrics/resumen`

```json
{
  "totalSolicitudes": 0,
  "solicitudesPendientes": 0,
  "solicitudesAprobadas": 0,
  "solicitudesRechazadas": 0
}
```

**Archivo (metadata)**

```json
{
  "id": "uuid",
  "solicitudId": "uuid",
  "nombreArchivo": "string",
  "contentType": "string",
  "subidoEn": "2026-05-26T12:00:00Z"
}
```

### 5.3 Reglas de dominio (UI debe respetar)

| Regla | Comportamiento backend |
|-------|------------------------|
| Creación | Estado inicial = `ENVIADA` |
| Estados terminales | `APROBADA`, `RECHAZADA` → no permiten más cambios |
| No retroceso | No se puede volver a `BORRADOR` desde otro estado |
| Paginación | `page` base 0, `size` default **10** (alineado con DAS) |

### 5.4 Errores (RFC 7807 ProblemDetail)

El frontend debe parsear respuestas de error así:

```json
{
  "type": "about:blank",
  "title": "Recurso no encontrado",
  "status": 404,
  "detail": "Solicitud no encontrada: <uuid>",
  "timestamp": "2026-05-26T12:00:00Z"
}
```

Validación de bean (`400`):

```json
{
  "title": "...",
  "status": 400,
  "detail": "Datos de entrada inválidos",
  "errors": [
    { "field": "titulo", "message": "..." }
  ]
}
```

**Convención UI (DAS):** mostrar `detail` y mensajes por campo; no exponer stack traces.

---

## 6. Matriz RBAC — fuente única

| Acción / Endpoint | COORDINADOR | GERENTE | SOLICITANTE |
|-------------------|:-----------:|:-------:|:-----------:|
| Crear solicitud | ✓ | ✓ | ✓ |
| Ver solicitud propia/detalle | ✓ | ✓ | ✓ |
| Listar todas | ✓ | ✓ | — |
| Cambiar estado | ✓ | ✓ | — |
| Métricas resumen | ✓ | ✓ | — |
| Subir archivo | ✓ | ✓ | ✓ |
| Listar archivos | ✓ | ✓ | ✓ |
| Descargar archivo | ✓ | ✓ | — |
| Exportar reportes (futuro HU) | ✓ | ✓ | — |

**Frontend (DAS):** ocultar botones según rol del token (ej. "Exportar" solo COORDINADOR/GERENTE).  
**Backend:** siempre validar con `@PreAuthorize` — la UI no es seguridad.

---

## 7. Kong Gateway — contrato compartido

| Plugin | Propósito |
|--------|-----------|
| `cors` | Orígenes del frontend dev/prod |
| `jwt` | Validación RS256; `key_claim_name: iss` |
| `rate-limiting` | 100 req/min (local) |
| `correlation-id` | Header `X-Correlation-Id` |
| `request-transformer` | Header `X-Gateway: Kong` |

**Archivos en repo backend:**

- `docker/kong/kong.services.yml` — servicios y plugins
- `docker/kong/docker-entrypoint-kong.sh` — inyecta consumer + `public.pem` al arrancar

Al añadir un nuevo microservicio al ecosistema: extender `kong.services.yml` (o archivo equivalente) con ruta `/api` o prefijo dedicado — **mantener un solo Gateway**.

---

## 8. Alineación de capas — Backend vs Frontend

| Capa (DAS / UML) | Backend (`solicitudes-service`) | Frontend (convención obligatoria) |
|------------------|--------------------------------|-----------------------------------|
| Presentación | `presentation.controller`, `dto`, `mapper` | `presentation/` pages, components |
| Aplicación | `application.service`, `port`, `command` | `application/` hooks, Zustand stores |
| Dominio | `domain.model`, `domain.service` | Tipos TS + validadores (sin lógica de infra) |
| Infraestructura | `infrastructure.*` | `infrastructure/` Axios, JWT interceptor, cache |
| Utils | `utils` | `utils/` constants, theme |

**Anti-patrón:** llamar `fetch` directo desde componentes — usar **service layer** + **AxiosClient** (diagrama frontend).

---

## 9. Caché y datos volátiles (coherencia UX)

| Caché | TTL | Invalidación | Implicación frontend |
|-------|-----|--------------|----------------------|
| `solicitud` | 5 min | POST/PUT/DELETE de esa solicitud | Tras mutación, invalidar query SWR/React Query |
| `metricas` | 1 min | Cualquier cambio de estado / create | Refetch dashboard cada ≤1 min o on-focus |

**SWR (DAS):** deduplicación y revalidación alineada con TTL backend; no asumir consistencia fuerte inmediata.

---

## 10. Variables de entorno — convención entre repos

### 10.1 Reglas de nomenclatura

- **MAYÚSCULAS** con `_` (snake case).
- **Sin secretos** en código ni en repos; solo `.env.example` con placeholders.
- Mismos **nombres semánticos** entre repos cuando aplique (ej. `JWT_KONG_ISSUER`).

### 10.2 Backend (`traceability-solicitudes-service`)

Ver `.env.example` completo. Mínimo para integración frontend:

```bash
KONG_PROXY_PORT=8000
JWT_KONG_ISSUER=traceability-dev
```

### 10.3 Frontend (plantilla sugerida — `traceability-frontend/.env.example`)

```bash
# API (siempre Kong en dev/prod)
NEXT_PUBLIC_API_BASE_URL=http://localhost:8000
NEXT_PUBLIC_API_VERSION=v1

# Opcional: issuer documentado para debugging
NEXT_PUBLIC_JWT_ISSUER=traceability-dev

# Paginación (debe coincidir con backend)
NEXT_PUBLIC_DEFAULT_PAGE_SIZE=10
```

---

## 11. Perfiles y entornos

| Entorno | Backend profile | Frontend | Infra |
|---------|-----------------|----------|-------|
| Tests CI | `test,inmemory` | `test` / mocks | Sin Docker |
| Dev local ligero | `inmemory` | mock API o Kong | Opcional |
| Dev Docker | `jpa` | contra Kong :8000 | compose completo |
| Producción | `jpa` + secrets manager | contra Kong | K8s (futuro DAS) |

---

## 12. Fuera de alcance actual (no asumir en otros repos)

| Elemento | Estado |
|--------|--------|
| Módulos Parametrizacion / Finanzas | Solo diagrama; sin API |
| Refresh tokens | Mencionado en DAS; **no implementado** |
| Login / emisión JWT en microservicio | Auth en Gateway; no hay `/auth/login` aquí |
| Exportación PDF/Excel (HU-06) | Futuro |
| Kafka / RabbitMQ | Futuro (mencionado en DAS performance) |
| Kubernetes | Futuro |

---

## 13. Checklist de consistencia para nuevo PR (cualquier repo)

- [ ] Rutas bajo `/api/v1` (o versión documentada).
- [ ] JSON camelCase coincide con §5.2.
- [ ] Roles usan exactamente: `COORDINADOR`, `GERENTE`, `SOLICITANTE`.
- [ ] JWT incluye `iss`, `sub`, `exp`, `roles`.
- [ ] Errores consumibles como ProblemDetail (backend) / mapeados en UI (frontend).
- [ ] Paginación `page`/`size` con default size=10.
- [ ] Fechas en ISO-8601 UTC (`Instant` / `toISOString()`).
- [ ] Sin secretos ni puertos hardcodeados.
- [ ] Docker images con tag fijo (no `latest`).
- [ ] Cambio arquitectónico → ADR + actualizar este `HANDOFF.md`.

---

## 14. Referencias rápidas en este repositorio

| Recurso | Ubicación |
|---------|-----------|
| README operativo | `README.md` |
| Variables entorno | `.env.example` |
| Claves dev RSA | `scripts/generate-dev-keys.sh` |
| JWT dev | `scripts/generate-dev-jwt.py` |
| Compose stack | `docker-compose.yml` |
| Migraciones BD | `src/main/resources/db/migration/` |
| OpenAPI | `/swagger-ui.html` (perfil `jpa`) |

---

## 15. Contacto / mantenimiento del handoff

| Acción | Responsable sugerido |
|--------|---------------------|
| Cambio en contrato API | Actualizar backend + este archivo + tipos TS frontend |
| Nuevo microservicio | ADR + fila en §2 + ruta Kong |
| Cambio de roles | §6 + DAS + ambos repos |

**Versión siguiente:** incrementar tabla §1 y documentar diff (ej. v1.1 — refresh tokens).

---

*Documento generado para alinear `traceability-solicitudes-service` con el DAS TraceAbility v2.0 y preparar `traceability-frontend`.*
