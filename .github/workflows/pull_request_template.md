# Pull Request — TraceAbility · Microservicio de Solicitudes

## Rama destino

<!-- Indica hacia qué rama va el PR y qué pipeline debe pasar. -->

- [ ] **`develop`** → dispara **CI** (lint, build, tests, Docker build, SonarQube)
- [ ] **`main`** → dispara **CD** (imagen Docker + deploy a producción, requiere aprobación manual)

---

## Historias de usuario cerradas

<!-- Una HU por línea. Formato: HU-XX: título exacto del backlog -->
<!-- Prioridades Must → Should → Could. Ejemplo: -->
<!-- HU-01: Registrar nueva solicitud de servicio -->

- HU-NN: [título]

---

## Descripción

<!-- Qué cambia, por qué, y qué capas o componentes toca.             -->
<!-- Menciona: SolicitudController / SolicitudService / SolicitudRepository / Frontend / Infra -->

---

## Ámbito del cambio

- [ ] `backend` — Spring Boot (controller / service / repository / domain)
- [ ] `frontend` — Next.js (pages / components / hooks / context)
- [ ] `caché` — Redis / Caffeine (@Cacheable, @CacheEvict, TTL)
- [ ] `seguridad` — JWT / RBAC / Spring Security / Rate Limiter
- [ ] `notificaciones` — SMTP / WebSocket / NotificationService
- [ ] `infra / CI-CD` — Dockerfile, Docker Compose, `.github/`, Kubernetes
- [ ] `documentación` — README, DAS, Swagger/OpenAPI

---

## Tipo de cambio

- [ ] `feat` — Nueva funcionalidad (HU nueva o parcial)
- [ ] `fix` — Corrección de bug
- [ ] `docs` — Solo documentación
- [ ] `refactor` — Refactor sin cambio funcional
- [ ] `test` — Añade o ajusta tests (JUnit / Jest / Playwright)
- [ ] `chore` — Mantenimiento (dependencias, configs, CI/CD)
- [ ] `perf` — Mejora de rendimiento (caché, queries JPA, paginación)
- [ ] `security` — Corrección o mejora de seguridad

---

## Criterios de aceptación

<!-- Copia los CA de la HU del Product Backlog y marca los cumplidos. -->
<!-- Ejemplo: CA-01: El sistema asigna automáticamente el consultor con menos carga -->

- [ ] CA-01: ...
- [ ] CA-02: ...
- [ ] CA-03: ...
- [ ] CA-04: ...
- [ ] CA-05: ...

---

## Calidad — Backend (`Spring Boot`)

<!-- Marca solo si modificaste el backend. -->

- [ ] Tests unitarios (JUnit + Mockito) añadidos o actualizados para el código nuevo
- [ ] `./mvnw test` pasa localmente sin fallos
- [ ] Cobertura revisada (`./mvnw test jacoco:report`) — mínimo 80 % en clases nuevas
- [ ] `./mvnw test -Dtest=*IntegrationTest` pasa (si toca endpoints HTTP)
- [ ] `./mvnw checkstyle:check` sin errores
- [ ] `./mvnw build` (Spring Boot) pasa sin warnings críticos
- [ ] Swagger/OpenAPI actualizado si se agrega o modifica un endpoint (`/swagger-ui.html`)
- [ ] Sin `System.out.println` — usar SLF4J (`log.info / log.error`)

---

## Calidad — Frontend (`Next.js`)

<!-- Marca solo si modificaste el frontend. -->

- [ ] Tests añadidos o actualizados (Jest + React Testing Library / Playwright)
- [ ] `npm run lint` (`next lint`) pasa sin errores
- [ ] `npx tsc --noEmit` pasa
- [ ] `npm run build` pasa
- [ ] `npm run test` pasa
- [ ] `npm run test:e2e` pasa (si cambia flujo de UI o navegación)
- [ ] Componentes nuevos documentados con PropTypes / TypeScript types
- [ ] Sin tokens, API keys ni variables de entorno hardcodeadas en el código

---

## Validación funcional

- [ ] Probado localmente con Docker Compose (`docker compose up -d postgres redis`)
- [ ] `GET /actuator/health` responde `{"status":"UP"}` (si toca backend)
- [ ] Flujo de solicitud verificado end-to-end (crear → asignar → cambiar estado)
- [ ] RBAC validado: el endpoint responde 403 con rol no autorizado
- [ ] Cache invalidado correctamente tras operaciones POST / PUT / DELETE
- [ ] Variables de entorno nuevas documentadas en `.env.example` y en el README
- [ ] Sin `.env`, credenciales, tokens ni secretos en el diff del PR

---

## Impacto en base de datos / infraestructura

- [ ] Sin cambios en esquema de BD
- [ ] Migración de BD incluida y probada (si aplica — indicar nombre del script)
- [ ] Sin cambios en variables de entorno requeridas
- [ ] Nueva variable de entorno añadida → documentada en `.env.example` y README
- [ ] Sin cambios en puertos o configuración de red
- [ ] Cambio en configuración de Redis / Caffeine (TTL, keys) → documentado

---

## Documentación

- [ ] `README.md` actualizado si cambia instalación, puertos, pipeline o comandos
- [ ] DAS actualizado si cambia la arquitectura o se toma una nueva decisión (ADR)
- [ ] Comentarios en código solo donde la lógica de negocio no sea obvia
- [ ] Mensajes de commit siguen Conventional Commits (`feat(solicitudes): ...`)

---

## Evidencia

<!-- Capturas de la UI, salida de tests en consola, curl de endpoints,  -->
<!-- o enlace al run de GitHub Actions / GitLab CI.                      -->

```
# Pega aquí logs, resultado de curl, o enlace al job de CI
```

---

## Notas para el revisor

<!-- Decisiones de diseño tomadas, deuda técnica generada, riesgos,     -->
<!-- o áreas donde quieres foco especial en la revisión.                -->
<!-- Ejemplo: "Usé Cache-Aside en MetricService porque los datos cambian -->
<!-- con baja frecuencia (TTL 1 min). Redis key pattern: métrica:{tipo}" -->