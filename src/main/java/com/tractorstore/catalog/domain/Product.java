package com.tractorstore.catalog.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Un tractor del catálogo, con sus variantes de color y highlights de venta.
 *
 * @param id identificador único del producto
 * @param name nombre comercial (p. ej. "SmartFarm Titan")
 * @param description descripción corta del producto
 * @param category categoría comercial (clásico o autónomo)
 * @param price precio único del producto, compartido por todas sus variantes
 * @param highlights lista de características destacadas mostradas en la página de producto
 * @param variants variantes de color disponibles; nunca vacía
 */
public record Product(
    String id,
    String name,
    String description,
    ProductCategory category,
    BigDecimal price,
    List<String> highlights,
    List<Variant> variants) {

  public Product {
    Objects.requireNonNull(id, "id no puede ser null");
    Objects.requireNonNull(name, "name no puede ser null");
    Objects.requireNonNull(description, "description no puede ser null");
    Objects.requireNonNull(category, "category no puede ser null");
    Objects.requireNonNull(price, "price no puede ser null");
    if (id.isBlank() || name.isBlank()) {
      throw new IllegalArgumentException("id y name no pueden estar vacíos");
    }
    if (price.signum() < 0) {
      throw new IllegalArgumentException("price no puede ser negativo: " + price);
    }
    highlights = List.copyOf(highlights);
    variants = List.copyOf(variants);
    if (variants.isEmpty()) {
      throw new IllegalArgumentException("un producto debe tener al menos una variante");
    }
  }

  /** Busca una variante de este producto por su SKU. */
  public Optional<Variant> variantBySku(String sku) {
    return variants.stream().filter(variant -> variant.sku().equals(sku)).findFirst();
  }
}
