# ServiPlus — Módulo de Solicitudes (Equipo 3)

Gestión del ciclo de vida de solicitudes de servicio técnico para la plataforma ServiPlus S.A., 
como parte de un ecosistema distribuido de cinco módulos para la administración integral de servicios.

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Status](https://img.shields.io/badge/status-En%20Desarrollo-orange.svg)]()
[![Quality Gate](https://img.shields.io/badge/quality%20gate-passed-brightgreen.svg)]()
[![Coverage Backend](https://img.shields.io/badge/coverage%20backend-82%25-brightgreen.svg)]()

## 📝 Descripción

**ServiPlus** es una plataforma empresarial orientada a la gestión de soporte técnico, compuesta por un ecosistema de microservicios independientes altamente cohesivos:

| Módulo | Repositorio / Estado |
|---|---|
| **Solicitudes** | *(este repositorio)* |
| Atenciones | [serviplus-atenciones](https://github.com/ServiPlus-S-A/api-atenciones) |
| Finanzas | [serviplus-finanzas](https://github.com/ServiPlus-S-A/api-finanzas) |
| Parametrización | [serviplus-parametrizacion](https://github.com/ServiPlus-S-A/api-parametrizacion) |
| Reportes | [serviplus-reportes](https://github.com/ServiPlus-S-A/api-reportes) |

Este repositorio implementa el **módulo de Solicitudes**: el punto de partida operativo del sistema. Permite registrar, clasificar, priorizar y administrar el ciclo de vida de los requerimientos de los clientes. El microservicio expone un API REST protegido mediante control de acceso basado en roles (RBAC), implementa estrategias híbridas de almacenamiento en caché para alta disponibilidad, y asegura resiliencia total mediante disyuntores (*Circuit Breakers*) al comunicarse con dependencias externas.

---

## 🏛️ Arquitectura del Sistema

El módulo opera como una célula backend desacoplada dentro de una red interna protegida, delegando la autenticación perimetral y el enrutamiento al API Gateway común.

```text
[ Cliente / Navegador ]
          │
          ▼ (HTTPS)
[ API Gateway (Kong) ] ───► Autenticación JWT, Rate Limiting y Enrutamiento Global
          │
          ▼ (API REST Interna / Red Confiable)
[ Backend Solicitudes (Spring Boot 3.x) ]
          │
          ├──► [ Caché L1 (Caffeine) ] ──► Acceso ultrarrápido en memoria local
          │
          ├──► [ Caché L2 (Redis) ] ────► Consistencia de caché distribuida
          │
          └──► [ Base de Datos (PostgreSQL / Supabase) ] ──► Persistencia Relacional
