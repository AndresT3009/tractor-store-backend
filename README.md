# tractor-store-backend

## Observabilidad (Fase B11)

- `./mvnw spring-boot:run` levanta también Postgres, Prometheus y Grafana (todos definidos en
  `docker-compose.yml`, gestionados por `spring-boot-docker-compose`).
- Prometheus: http://localhost:9090 (scrapea `/actuator/prometheus` del backend en el host cada 5s).
- Grafana: http://localhost:3001 (acceso anónimo habilitado como Viewer; admin/admin si hace falta
  editar). El dashboard "Tractor Store Backend" se provisiona solo, con latencia p95, requests/s,
  errores 5xx y conexiones activas del pool.
- Para generar carga y ver las métricas moverse en vivo: `k6 run observability/load-test/smoke-test.js`
  (requiere [k6](https://k6.io/) instalado; usa `-e BASE_URL=...` si el backend no corre en
  `localhost:8080`).
- Cada request trae o genera un header `X-Trace-Id`, que queda en el MDC de todos los logs de esa
  request (`traceId`) para poder correlacionarlos.