package com.tractorstore.catalog.domain;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * El catálogo completo de productos y tiendas, con las consultas de negocio que necesita el equipo
 * Explore: listar por categoría, buscar un producto, y recomendar por color.
 *
 * <p>Es un objeto de dominio puro: no sabe nada de HTTP, JPA ni Spring. La carga de datos (memoria,
 * JSON o PostgreSQL más adelante) es responsabilidad de una capa de infraestructura que construye
 * esta clase con los datos ya resueltos.
 */
public final class ProductCatalog {

  private final List<Product> products;
  private final List<Store> stores;

  public ProductCatalog(List<Product> products, List<Store> stores) {
    this.products = List.copyOf(products);
    this.stores = List.copyOf(stores);
  }

  /** Productos de una categoría, o todo el catálogo si {@code category} es {@code null}. */
  public List<Product> byCategory(ProductCategory category) {
    if (category == null) {
      return products;
    }
    return products.stream().filter(product -> product.category() == category).toList();
  }

  public Optional<Product> findById(String productId) {
    return products.stream().filter(product -> product.id().equals(productId)).findFirst();
  }

  public List<Store> stores() {
    return stores;
  }

  /**
   * Recomienda hasta {@code limit} variantes de color similar a las de los SKUs indicados,
   * excluyendo las de los propios productos ya seleccionados.
   *
   * <p>Si ninguno de los {@code selectedSkus} existe en el catálogo, devuelve una lista vacía: no
   * hay ninguna base de color sobre la que recomendar.
   */
  public List<Variant> recommend(List<String> selectedSkus, int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("limit debe ser mayor que cero: " + limit);
    }

    List<ProductVariant> selected = resolveVariants(selectedSkus);
    if (selected.isEmpty()) {
      return List.of();
    }

    Set<String> excludedProductIds =
        selected.stream().map(ProductVariant::productId).collect(Collectors.toSet());

    return candidatesExcluding(excludedProductIds).stream()
        .sorted(Comparator.comparingDouble(candidate -> minDistanceTo(candidate, selected)))
        .limit(limit)
        .map(ProductVariant::variant)
        .toList();
  }

  private List<ProductVariant> resolveVariants(List<String> skus) {
    Set<String> requested = new HashSet<>(skus);
    return products.stream()
        .flatMap(
            product ->
                product.variants().stream()
                    .filter(variant -> requested.contains(variant.sku()))
                    .map(variant -> new ProductVariant(product.id(), variant)))
        .toList();
  }

  private List<ProductVariant> candidatesExcluding(Set<String> excludedProductIds) {
    return products.stream()
        .filter(product -> !excludedProductIds.contains(product.id()))
        .flatMap(
            product ->
                product.variants().stream()
                    .map(variant -> new ProductVariant(product.id(), variant)))
        .toList();
  }

  private double minDistanceTo(ProductVariant candidate, List<ProductVariant> selected) {
    return selected.stream()
        .mapToDouble(reference -> ColorDistance.between(candidate.colorHex(), reference.colorHex()))
        .min()
        .orElseThrow();
  }

  private record ProductVariant(String productId, Variant variant) {
    String colorHex() {
      return variant.colorHex();
    }
  }
}
