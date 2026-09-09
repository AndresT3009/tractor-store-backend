## Qué cambia y por qué

## Cómo probarlo

## Checklist

- [ ] `./mvnw verify` pasa en local (tests + ArchUnit + Modulith)
- [ ] `./mvnw spotless:check` pasa en local
- [ ] Si el cambio toca un módulo existente, revisé que no rompa sus fronteras (`ArchitectureTest`,
      `ModularityTests`)
- [ ] Si el cambio agrega/modifica un endpoint, actualicé la tabla de endpoints en `README.md`
