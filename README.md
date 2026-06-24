# 📋 TraceAbility — Microservicio de Solicitudes

> Microservicio REST para la gestión integral del ciclo de vida de solicitudes de servicio (soporte técnico, mantenimiento y consultoría), con asignación automática de consultores, caché distribuida y trazabilidad completa por auditoría.

[![Pipeline CI](https://img.shields.io/badge/pipeline-passing-brightgreen)]()
[![Cobertura](https://img.shields.io/badge/cobertura-mínimo%2080%25-yellow)]()
[![Versión](https://img.shields.io/badge/versión-1.3-blue)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)]()
[![Next.js](https://img.shields.io/badge/Next.js-14-black?logo=next.js)]()
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)]()

---

## 📑 Tabla de contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Arquitectura](#arquitectura)
- [Stack tecnológico](#stack-tecnológico)
- [Roles y funcionalidades](#roles-y-funcionalidades)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución local](#instalación-y-ejecución-local)
- [Variables de entorno](#variables-de-entorno)
- [Pipeline CI/CD](#pipeline-cicd)
- [Convención de ramas](#convención-de-ramas)
- [Cómo contribuir (Pull Request)](#cómo-contribuir-pull-request)
- [Equipo](#equipo)

---

## 📌 Descripción del proyecto

**TraceAbility** es una plataforma modular de gestión de servicios tecnológicos. Este repositorio contiene **únicamente el microservicio de Solicitudes**, responsable de:

- Registro y ciclo de vida completo de solicitudes (Pendiente → Asignada → En Ejecución → Resuelta / Cancelada).
- Asignación automática de consultores según capacitación y carga de trabajo.
- Notificaciones asíncronas a clientes, consultores y coordinadores vía plataforma y correo.

**Autores:** Andres Fernando Perea · Nicolas Mosorongo · Jhoan Sebastian Fernandez  
**Fecha de creación:** 20 de Mayo de 2026 | **Versión DAS:** 1.1

---

## 🏗️ Arquitectura

El microservicio sigue una **arquitectura de capas** (Presentation → Service → Data Access) expuesto vía **Kong API Gateway** con autenticación JWT + RBAC.

```
Cliente / Consultor / Coordinador
          │
          ▼
    Kong API Gateway  ──── JWT (RS256) + Rate Limiter + CORS
          │
          ▼
 ┌────────────────────────────────────────────────────┐
 │              Microservicio de Solicitudes          │
 │                                                    │
 │  Presentation Layer                                │
 │    SolicitudController · ExportController          │
 │    MetricsController   · FileController            │
 │                                                    │
 │  Service Layer                                     │
 │    SolicitudService · ExportService                │
 │    NotificationService · AuditService              │
 │    CacheService      · AuthService                 │
 │                                                    │
 │  Data Access Layer                                 │
 │    SolicitudRepository · FileRepository            │
 │    JPA + PostgreSQL                                │
 └────────────────────────────────────────────────────┘
          │                    │
          ▼                    ▼
     PostgreSQL            Redis Cache
                               │
                          Caffeine (L1)

 Frontend: Next.js 14
   DashboardPage · SolicitudesPage · ReportesPage
   DataTable · FiltersComponent
   AuthContext · useSolicitudes · useMetrics · JWTInterceptor
```

### Decisiones arquitectónicas clave (ADRs)

| ADR | Decisión | Justificación |
|-----|----------|---------------|
| ADR-001 | Redis + Caffeine para caché distribuida | TTL 5 min en lecturas, invalidación por escritura, compartido entre instancias |
| ADR-006 | JWT RS256 + RBAC con `@PreAuthorize` | Autenticación stateless, clave pública en servicio, escalable horizontalmente |
| — | Circuit Breaker con Resilience4j | Tolerancia a fallos en llamadas externas y timeouts |

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| **Backend** | Spring Boot 3.x · Spring Security · Spring Cache · JPA/Hibernate |
| **Frontend** | Next.js 14 · React · Zustand · Axios · SWR |
| **Base de datos** | PostgreSQL |
| **Caché** | Redis + Caffeine (multinivel) |
| **API Gateway** | Kong Gateway |
| **Seguridad** | JWT RS256 · RBAC · Rate Limiting · Spring Security |
| **Testing** | JUnit · Mockito · Spring Boot Test |
| **Documentación API** | Swagger / OpenAPI |
| **Contenedores** | Docker · Kubernetes |
| **Observabilidad** | SLF4J · Prometheus · Jaeger/DataDog |
| **Notificaciones** | SMTP (correo) + WebSocket/SSE (plataforma) |
| **Almacenamiento** | Bucket (S3/GCS) para adjuntos |

---

## 👥 Roles y funcionalidades

El sistema maneja tres roles principales. A continuación un resumen de las historias de usuario por rol (Product Backlog v1.3):

### 🧑 Cliente
| HU | Funcionalidad | Prioridad |
|----|--------------|-----------|
| HU-01 | Registrar nueva solicitud de servicio con asignación automática de consultor | Must |
| HU-02 | Visualizar historial personal de solicitudes | Must |
| HU-03 | Filtrar solicitudes por estado, tipo, folio y rango de fechas | Must |
| HU-04 | Cancelar solicitudes en estado Pendiente o Sin asignación | Should |
| HU-05 | Ver detalle completo de una solicitud (solo lectura) | Must |
| HU-06 | Agregar detalles y adjuntos a solicitudes abiertas | Could |
| HU-07 | Recibir notificación automática de creación de solicitud | Could |
| HU-08 | Recibir notificación automática de reasignación de consultor | Could |

### 🗂️ Coordinador
| HU | Funcionalidad | Prioridad |
|----|--------------|-----------|
| HU-09 | Ver estado de todas las solicitudes en tablero de control | Must |
| HU-10 | Filtrar todas las solicitudes con criterios globales | Should |
| HU-11 | Ver detalle de una solicitud específica | Must |
| HU-12 | Cancelar solicitudes con motivo y tipo de falla | Should |
| HU-13 | Reasignar consultor a solicitudes Pendientes o Sin asignación | Should |
| HU-14 | Registrar notas de progreso y hallazgos técnicos | Could |
| HU-15 | Recibir alertas de solicitudes sin asignación cada 4 horas | Should |

### 🔧 Consultor
| HU | Funcionalidad | Prioridad |
|----|--------------|-----------|
| HU-16 | Recibir notificación de asignación de solicitud | Should |
| HU-17 | Ver lista de solicitudes asignadas | Must |
| HU-18 | Filtrar solicitudes bajo su cargo | Should |
| HU-19 | Ver detalle de una solicitud (solo lectura) | Must |
| HU-20 | Cambiar estado de solicitud con resumen de atención | Must |
| HU-21 | Registrar notas técnicas inmutables en bitácora | Could |

---

## ✅ Requisitos previos

- Java 17+
- Node.js 18+
- Docker y Docker Compose
- PostgreSQL 15+ (o vía Docker)
- Redis 7+ (o vía Docker)
- Maven 3.9+

---

## ⚙️ Instalación y ejecución local

### 1. Clonar el repositorio

```bash
git clone https://github.com/org/traceability-solicitudes.git
cd traceability-solicitudes
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# Editar .env con tus valores locales
```

### 3. Levantar infraestructura con Docker Compose

```bash
docker compose up -d postgres redis
```

### 4. Ejecutar el backend

```bash
./mvnw spring-boot:run
# API disponible en http://localhost:8080
# Swagger UI en http://localhost:8080/swagger-ui.html
```

### 5. Ejecutar el frontend

```bash
cd frontend
npm install
npm run dev
# App disponible en http://localhost:3000
```

### 6. Ejecutar tests

```bash
# Backend
./mvnw test

# Frontend
npm run test
```

---

## 🔐 Variables de entorno

Copia `.env.example` a `.env` y completa los valores. **Nunca comitees el `.env` con valores reales.**

| Variable | Descripción | Requerida |
|----------|-------------|-----------|
| `DB_URL` | URL JDBC de PostgreSQL (ej: `jdbc:postgresql://localhost:5432/traceability`) | ✅ |
| `DB_USERNAME` | Usuario de la base de datos | ✅ |
| `DB_PASSWORD` | Contraseña de la base de datos | ✅ |
| `REDIS_HOST` | Host del servidor Redis | ✅ |
| `REDIS_PORT` | Puerto Redis (default: `6379`) | ✅ |
| `JWT_PUBLIC_KEY` | Clave pública RSA para validación de tokens JWT | ✅ |
| `SMTP_HOST` | Host del servidor de correo | ✅ |
| `SMTP_PORT` | Puerto SMTP | ✅ |
| `SMTP_USER` | Usuario SMTP | ✅ |
| `SMTP_PASSWORD` | Contraseña SMTP | ✅ |
| `STORAGE_BUCKET` | Nombre del bucket para adjuntos (S3/GCS) | ✅ |
| `KONG_GATEWAY_URL` | URL del API Gateway | ✅ |
| `LOG_LEVEL` | Nivel de logs: `info` / `debug` / `warn` | ❌ |
| `CACHE_TTL_SECONDS` | TTL de caché (default: `300`) | ❌ |

---

## 🔄 Pipeline CI/CD

El pipeline está configurado para ejecutarse automáticamente en cada push y merge request.

| Stage | Descripción | Trigger |
|-------|-------------|---------|
| `lint` | Análisis estático (ESLint + Checkstyle) | Todo push |
| `test` | Tests unitarios e integración (JUnit + Jest) | Todo push |
| `sonar` | Análisis de calidad con SonarQube | Todo push a `develop` / `main` |
| `build` | Build imagen Docker | Merge a `develop` o `main` |
| `deploy-staging` | Deploy automático a staging | Merge a `develop` |
| `deploy-prod` | Deploy a producción con aprobación manual | Tag `v*.*.*` |

### Deploy a producción

```bash
# Crear y publicar tag de versión
git tag v1.3.0
git push origin v1.3.0
```

> **Regla:** No se permite merge a `main` si el pipeline no está en verde y sin la aprobación de al menos 1 reviewer.

---

## 🌿 Convención de ramas

```
main          ← producción (protegida)
develop       ← integración (protegida)
Nmosorongo    ← Desarrollo 
Aperea        ← Desarrollo 
Jfernandez    ← Desarrollo 
```

**Commits:** seguir [Conventional Commits](https://www.conventionalcommits.org/)

```
feat(solicitudes): agregar asignación automática de consultor HU-01
fix(cache): corregir invalidación de caché en cancelación HU-04
test(solicitud-service): agregar pruebas unitarias para cambio de estado
chore(ci): actualizar imagen base de Docker a Java 17
```

---

## 🤝 Cómo contribuir (Pull Request)

Antes de abrir un PR asegúrate de:

1. Crear la rama desde `develop`: `git checkout -b feature/HU-XX-mi-funcionalidad`
2. Verificar que los tests pasan localmente: `./mvnw test && npm run test`
3. Que el pipeline del repositorio está en verde
4. Abrir el PR usando la **plantilla oficial del proyecto** (`.github/pull_request_template.md`)

La plantilla solicita: descripción, ticket relacionado, tipo de cambio, cómo se probó, evidencia y checklist de calidad. Un PR sin la plantilla completa será rechazado durante la revisión.

**Mínimo de aprobaciones:** 1 reviewer del equipo antes de hacer merge.

---

## 👥 Equipo

| Nombre | 
|--------|
| Andres Fernando Perea |
| Nicolas Mosorongo | 
| Jhoan Sebastian Fernandez | 

---

> **Nota DevOps:** Este README es un artefacto vivo. Debe actualizarse en el mismo PR donde se modifique la arquitectura, se agreguen variables de entorno o cambie el proceso de despliegue. Un README desactualizado es deuda técnica.


## Licencia

Codigo privado - `UNLICENSED` (ver `package.json` en cada servicio).