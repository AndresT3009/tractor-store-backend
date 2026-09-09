# Despliegue (Railway)

## Servicios a crear

Dentro de un mismo proyecto de Railway:

1. **Postgres** (plugin nativo de Railway — "New" → "Database" → "PostgreSQL").
2. **backend**: "New" → "GitHub Repo" → este repo. Railway detecta el `Dockerfile` y
   `railway.json` solo (build en modo `DOCKERFILE`, healthcheck en `/actuator/health`).

## Variables de entorno del servicio `backend`

Railway permite referenciar variables de otro servicio del mismo proyecto con
`${{NombreDelServicio.VARIABLE}}`. El plugin de Postgres expone `PGHOST`, `PGPORT`, `PGDATABASE`,
`PGUSER`, `PGPASSWORD` — pero `spring.datasource.url` necesita el esquema `jdbc:postgresql://`, no
el `postgres://` que trae el `DATABASE_URL` que da Railway por defecto. Por eso se arma a mano:

| Variable | Valor |
| --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `DATABASE_USERNAME` | `${{Postgres.PGUSER}}` |
| `DATABASE_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `CORS_ALLOWED_ORIGINS` | las URLs públicas de los 4 servicios del frontend, separadas por coma (ver `DEPLOYMENT.md` del repo `tractor-store-frontend`) — se agrega **después** de desplegar el frontend, porque hasta entonces esas URLs no existen |

`server.port` ya lee `${PORT:8080}` (`application.yml`), así que no hace falta configurar el
puerto a mano — Railway inyecta `PORT` automáticamente.

Flyway corre sus migraciones al arrancar (`spring-boot-starter-flyway`, sin configuración
adicional): el primer deploy deja el esquema listo solo.

## Verificar

- `https://<dominio-de-railway>/actuator/health` → `{"status":"UP"}`.
- `https://<dominio-de-railway>/swagger-ui.html` → Swagger UI cargando la spec real.
- Revisar logs del deploy: deben aparecer las migraciones de Flyway (`V1__create_catalog_schema`,
  etc.) corriendo una sola vez.

## Pendiente tras el primer deploy

Una vez el frontend esté desplegado (ver su propio `DEPLOYMENT.md`), volver a este servicio y
completar `CORS_ALLOWED_ORIGINS` con las 4 URLs reales — sin esto, el navegador bloquea las
llamadas del frontend a la API por CORS aunque el backend esté sano.
