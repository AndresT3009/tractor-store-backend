package com.tractorstore.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Un pedido confirmado: datos del cliente, tienda de recogida y las líneas compradas.
 *
 * <p>Referencia la tienda solo por {@code storeId}, sin depender de la clase {@code Store} del
 * módulo catalog: los módulos no comparten entidades entre sí.
 *
 * @param id identificador único del pedido
 * @param firstName nombre de quien compra
 * @param lastName apellido de quien compra
 * @param storeId identificador de la tienda elegida para recoger el pedido
 * @param lines líneas del pedido; nunca vacía
 * @param placedAt instante en que se confirmó el pedido
 */
public record Order(
    String id,
    String firstName,
    String lastName,
    String storeId,
    List<OrderLine> lines,
    Instant placedAt) {

  public Order {
    Objects.requireNonNull(id, "id no puede ser null");
    Objects.requireNonNull(firstName, "firstName no puede ser null");
    Objects.requireNonNull(lastName, "lastName no puede ser null");
    Objects.requireNonNull(storeId, "storeId no puede ser null");
    Objects.requireNonNull(placedAt, "placedAt no puede ser null");
    if (id.isBlank() || firstName.isBlank() || lastName.isBlank() || storeId.isBlank()) {
      throw new IllegalArgumentException("id, firstName, lastName y storeId no pueden ser vacíos");
    }
    lines = List.copyOf(lines);
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("un pedido debe tener al menos una línea");
    }
  }

  public BigDecimal totalPrice() {
    return lines.stream().map(OrderLine::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
