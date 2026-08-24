package com.tractorstore.inventory.domain;

import java.util.Objects;

/**
 * Cantidad disponible de una variante concreta, identificada por su SKU.
 *
 * @param sku identificador de la variante (definida en el módulo catalog, referenciada aquí solo
 *     por su clave, sin depender de la clase Variant de ese módulo)
 * @param quantityAvailable unidades disponibles; nunca negativa
 */
public record StockLevel(String sku, int quantityAvailable) {

  public StockLevel {
    Objects.requireNonNull(sku, "sku no puede ser null");
    if (sku.isBlank()) {
      throw new IllegalArgumentException("sku no puede estar vacío");
    }
    if (quantityAvailable < 0) {
      throw new IllegalArgumentException(
          "quantityAvailable no puede ser negativo: " + quantityAvailable);
    }
  }

  public boolean isAvailable() {
    return quantityAvailable > 0;
  }
}
