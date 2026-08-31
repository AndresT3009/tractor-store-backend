# ADR 0001: Monolito modular (Spring Modulith) en vez de microservicios

## Estado

Aceptado.

## Contexto

La guía del reto (sección 4.2 y Fase B9) deja explícitamente la puerta abierta a dos caminos:
microservicios reales o un monolito con límites de módulo bien definidos, usando Spring Modulith para
hacerlos cumplir. El dominio tiene cuatro áreas razonablemente independientes (catálogo, inventario,
carrito, pedidos), lo cual en principio encajaría con microservicios.

## Decisión

Se implementó un monolito modular: un único proceso Spring Boot desplegable, con los cuatro módulos
de negocio (`catalog`, `inventory`, `cart`, `order`) como paquetes Java separados bajo
`com.tractorstore`, cada uno cerrado por defecto por Spring Modulith (solo su paquete raíz o lo
expuesto vía `@NamedInterface` es visible para los demás). `ModularityTests`
(`ApplicationModules.verify()`) y `ArchitectureTest` (ArchUnit) corren en cada build y fallan si algún
módulo empieza a alcanzar internals de otro.

## Por qué

- El equipo es de una persona y el alcance es un reto técnico de tiempo acotado: el costo operativo de
  microservicios reales (service discovery, múltiples pipelines de despliegue, versionado de contratos
  entre servicios, trazas distribuidas obligatorias desde el día uno) no tiene contrapartida — no hay
  equipos distintos por módulo, ni necesidad de escalar un módulo independientemente de los demás a
  corto plazo.
- Los cuatro módulos comparten el mismo modelo transaccional y la misma base de datos lógica
  (PostgreSQL con Flyway); partirlos en servicios separados habría exigido resolver consistencia
  distribuida (sagas, outbox real) para un beneficio que el propio alcance del reto no demanda.
- Spring Modulith da la mayor parte del beneficio arquitectónico de microservicios (límites
  explícitos, sin acoplar internals, comunicación por eventos en vez de llamadas directas entre casos
  de uso — ver [ADR 0002](0002-eventos-de-dominio-antes-de-broker-externo.md)) sin pagar el costo
  operativo, y **verificado en cada build** (no solo por convención/code review).
- El camino de migración queda abierto a propósito: si algún día un módulo necesitara desplegarse por
  separado, los límites ya existen en el código (paquetes, `@NamedInterface`, eventos de dominio en vez
  de llamadas directas) — la migración sería extraer un módulo, no rediseñar límites que nunca
  existieron.

## Consecuencias

- Un solo pipeline de CI/CD, un solo Dockerfile, un solo `docker compose up` para levantar todo.
- Los cuatro módulos comparten el mismo ciclo de vida de despliegue: no se puede desplegar `cart` sin
  desplegar también `catalog`. Aceptable dado el contexto del reto.
- Si en el futuro un módulo necesita escalar independientemente (por ejemplo, `catalog` con mucho más
  tráfico de lectura que `order`), la migración a un servicio separado parte de límites ya explícitos
  en el código, no de una base de código sin fronteras.
