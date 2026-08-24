package com.tractorstore.catalog.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Una variante comprable de un {@link Product}: un color concreto con su propio SKU.
 *
 * @param sku identificador único de la variante, usado en carrito, inventario y pedidos
 * @param colorName nombre comercial del color (p. ej. "Sunset Copper")
 * @param colorHex color en formato hexadecimal ("#RRGGBB"), usado para calcular recomendaciones
 * @param imageUrl imagen representativa de esta variante
 */
public record Variant(String sku, String colorName, String colorHex, String imageUrl) {

  private static final Pattern HEX_COLOR = Pattern.compile("^#[0-9A-Fa-f]{6}$");

  public Variant {
    requireNonBlank(sku, "sku");
    requireNonBlank(colorName, "colorName");
    requireNonBlank(colorHex, "colorHex");
    requireNonBlank(imageUrl, "imageUrl");
    if (!HEX_COLOR.matcher(colorHex).matches()) {
      throw new IllegalArgumentException("colorHex debe tener el formato #RRGGBB: " + colorHex);
    }
  }

  private static void requireNonBlank(String value, String fieldName) {
    Objects.requireNonNull(value, fieldName + " no puede ser null");
    if (value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " no puede estar vacío");
    }
  }
}
