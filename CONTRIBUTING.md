# Contribuir

## Flujo de trabajo

1. Ramas desde `develop`: `feat/...`, `fix/...`, `refactor/...`.
2. Commits en español, formato [Conventional Commits](https://www.conventionalcommits.org/es/):
   `tipo(módulo): descripción`. Ejemplos: `feat(cart): agregar endpoint de mini-carrito`,
   `fix(order): validar sesión de carrito antes de crear pedido`.
3. Antes de abrir el PR: `./mvnw verify` (tests + ArchUnit + Modulith) y `./mvnw spotless:check`
   deben pasar en local — el CI corre lo mismo y falla igual, pero es más rápido detectarlo antes.
4. PR contra `develop`, usando la plantilla. `main` solo recibe merges desde `develop` para release.

## Estilo de código

`spotless-maven-plugin` (google-java-format) formatea automáticamente:

```bash
./mvnw spotless:apply
```

`./mvnw spotless:check` (parte de `verify`) falla el build si hay algo sin formatear.

## Arquitectura

Antes de agregar una dependencia entre módulos (`cart` → `inventory`, por ejemplo), revisar
`ArchitectureTest` y `ModularityTests`: si el nuevo acceso rompe el aislamiento de un módulo, esos
tests fallan con el paquete y la regla exacta que se violó. Ver `docs/adr/` para el razonamiento
detrás de las fronteras actuales antes de proponer moverlas.
