import http from "k6/http";
import { check, sleep } from "k6";

// Fase B11: genera tráfico contra los endpoints reales del Tractor Store para observar
// latencia p95 y tasa de errores en el dashboard de Grafana mientras corre.
// Uso: k6 run observability/load-test/smoke-test.js
//      k6 run -e BASE_URL=http://localhost:8080 observability/load-test/smoke-test.js

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export const options = {
  vus: 10,
  duration: "30s",
};

export default function () {
  const home = http.get(`${BASE_URL}/api/catalog/home`);
  check(home, { "home devuelve 200": (r) => r.status === 200 });

  const categories = http.get(`${BASE_URL}/api/catalog/categories/all`);
  check(categories, { "categories devuelve 200": (r) => r.status === 200 });

  const product = http.get(`${BASE_URL}/api/catalog/products/smartfarm-titan`);
  check(product, { "product devuelve 200": (r) => r.status === 200 });

  const stock = http.get(`${BASE_URL}/api/inventory/SF-TITAN-COPPER`);
  check(stock, { "stock devuelve 200": (r) => r.status === 200 });

  sleep(1);
}
