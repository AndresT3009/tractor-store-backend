package com.tractorstore.order.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Una línea de pedido confirmada, con el precio congelado en el momento de la compra.
 *
 * <p>Se modela por separado de {@code CartLineItem} (módulo cart) a propósito: un pedido es una
 * fotografía histórica y no debe cambiar si el precio del producto cambia después en el catálogo.
 *
 * @param sku identificador de la variante comprada
 * @param quantity unidades compradas; siempre mayor que cero
 * @param unitPrice precio unitario en el momento del pedido; nunca negativo
 */
public record OrderLine(String sku, int quantity, BigDecimal unitPrice) {

  public OrderLine {
    Objects.requireNonNull(sku, "sku no puede ser null");
    Objects.requireNonNull(unitPrice, "unitPrice no puede ser null");
    if (sku.isBlank()) {
      throw new IllegalArgumentException("sku no puede estar vacío");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity debe ser mayor que cero: " + quantity);
    }
    if (unitPrice.signum() < 0) {
      throw new IllegalArgumentException("unitPrice no puede ser negativo: " + unitPrice);
    }
  }

  public BigDecimal subtotal() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }
}
