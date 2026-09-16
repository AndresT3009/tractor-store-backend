package com.tractorstore.cart.domain;

import java.util.Objects;

/**
 * Una línea del carrito: una variante y cuántas unidades se quieren.
 *
 * @param sku identificador de la variante (definida en el módulo catalog)
 * @param quantity unidades solicitadas; siempre mayor que cero
 */
public record CartLineItem(String sku, int quantity) {

  public CartLineItem {
    Objects.requireNonNull(sku, "sku no puede ser null");
    if (sku.isBlank()) {
      throw new IllegalArgumentException("sku no puede estar vacío");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity debe ser mayor que cero: " + quantity);
    }
  }

  CartLineItem withQuantity(int newQuantity) {
    return new CartLineItem(sku, newQuantity);
  }
}
