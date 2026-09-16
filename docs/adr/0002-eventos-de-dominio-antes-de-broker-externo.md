# ADR 0002: Eventos de dominio internos (Spring Modulith) antes que un broker externo

## Estado

Aceptado.

## Contexto

Al confirmar un pedido (`POST /api/orders`), el carrito de esa sesión debe vaciarse. La primera
implementación (Fase B7) hacía esto con `OrderController` llamando directamente a
`CartService.clear()` después de `OrderService.placeOrder()` — un acoplamiento directo de `order`
hacia `cart` solo para un efecto secundario, no para leer datos que `order` genuinamente necesita.

## Decisión

`order` publica un evento de dominio `OrderPlaced(orderId, sessionId)` (definido en
`shared.events`, no en `order.domain`, precisamente para no crear un ciclo: `order` ya depende de
`cart` para leer el carrito, así que si el evento viviera en `order`, `cart` tendría que importarlo y
se cerraría el ciclo `order↔cart`). `cart.application.CartCheckoutListener` escucha ese evento con
`@TransactionalEventListener` y vacía el carrito — `order` ya no conoce `CartService` para esto.

Se usa el soporte de eventos de aplicación de Spring (vía Spring Modulith), no un broker externo tipo
Kafka/RabbitMQ ni el patrón Outbox con tabla propia.

## Por qué

- El efecto (vaciar el carrito) ocurre en el mismo proceso, la misma base de datos, la misma
  transacción lógica que el resto del checkout — no hay necesidad de resiliencia entre procesos ni de
  garantías de entrega distribuida que un broker o un outbox real resolverían.
- `@TransactionalEventListener(phase = AFTER_COMMIT)` da la garantía que sí importa aquí: el carrito
  solo se vacía si el pedido efectivamente se guardó. Si `placeOrder` falla, el listener nunca se
  invoca y el carrito queda intacto (comportamiiento correcto sin coordinación adicional).
- Introducir un broker externo o un outbox pattern para un evento intra-proceso sería una dependencia
  operativa (broker adicional para desplegar y monitorear, o tabla de outbox + poller) sin beneficio
  real a esta escala — ver también [ADR 0001](0001-monolito-modular-y-no-microservicios.md).
- El punto de extensión ya existe si en el futuro aparece un segundo consumidor de `OrderPlaced` (por
  ejemplo, notificaciones): se agrega otro `@EventListener`, sin tocar `OrderService`.

## Consecuencias

- El evento vive en `shared` (módulo `OPEN`), no en `order`: cualquier módulo puede escucharlo sin que
  `order` necesite conocer a sus consumidores.
- Si en algún momento el checkout necesitara notificar a un sistema *externo* (un ERP, un servicio de
  email transaccional en otro proceso), ahí sí correspondería introducir un outbox real o un broker —
  hoy no hay ese caso de uso.
- Un test de integración con Testcontainers (`CheckoutEventIntegrationTest`) ejercita el flujo
  completo con PostgreSQL real, no solo con mocks, precisamente porque el comportamiento transaccional
  (`AFTER_COMMIT` + `REQUIRES_NEW`) no se puede validar de forma confiable sin una base de datos real.
