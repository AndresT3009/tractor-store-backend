# ADR 0003: Estrategia de migraciones con Flyway

## Estado

Aceptado.

## Contexto

El esquema de PostgreSQL (`catalog_product`, `catalog_variant`, `catalog_store`, `inventory_stock`,
`cart_line_item`, `order_order`, `order_line`) necesita crearse y poblarse de forma reproducible en
cualquier entorno: la máquina de un desarrollador, CI, o un despliegue real. Hibernate puede generar
DDL automáticamente (`ddl-auto: update`/`create`), pero eso deja el esquema real a merced de lo que
Hibernate infiera de las entidades JPA en cada arranque.

## Decisión

Flyway es la única fuente de verdad del esquema. `spring.jpa.hibernate.ddl-auto` está fijo en
`validate` en **todos** los perfiles (`dev`, `test`, `prod`): Hibernate nunca crea ni modifica tablas,
solo valida que las entidades coincidan con lo que Flyway ya aplicó. Las migraciones viven en
`src/main/resources/db/migration/` versionadas (`V1__...` a `V6__...`), una por cambio de esquema
(schema) o de datos semilla (seed), y se aplican automáticamente al arrancar la aplicación —tanto en
local como en el contenedor Docker— sin ningún script manual.

## Por qué

- `ddl-auto: validate` en todos los ambientes (no solo prod) fue deliberado: si el esquema y las
  entidades JPA divergen, el error aparece inmediatamente en local o en el primer test de integración
  con Testcontainers, no la primera vez que alguien despliega a un ambiente con `ddl-auto` distinto.
  Correr el mismo modo en todos lados elimina una clase entera de "funciona en mi máquina".
- Las migraciones versionadas dan un historial auditable de cómo llegó el esquema a su estado actual
  (`flyway_schema_history`), algo que `ddl-auto: update` no ofrece — no hay forma de saber, mirando
  solo las entidades JPA, qué cambió y cuándo.
- Al no ejecutar nunca scripts manuales, el mismo mecanismo que crea el esquema en el equipo de un
  desarrollador es el que lo crea en el contenedor Docker (`Dockerfile` / `docker-compose.yml`) y en
  cada test de integración (Testcontainers arranca un Postgres real y Flyway corre las mismas
  migraciones ahí) — un único camino, no uno "de desarrollo" y otro "de producción".

## Consecuencias

- Cualquier cambio de esquema requiere una migración nueva (`V7__...`, etc.), nunca editar una ya
  aplicada — Flyway rechaza el arranque si el checksum de una migración ya aplicada cambia.
- El seed de datos de muestra (`V2__seed_catalog_data.sql`, `V4__seed_inventory_data.sql`) vive en el
  mismo mecanismo de migraciones que el esquema. Es apropiado para el alcance de este reto (catálogo
  de muestra fijo); un despliegue real probablemente separaría datos semilla de cambios de esquema.
