package com.tractorstore.catalog.application;

import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCatalog;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.domain.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Orquesta {@link ProductCatalog} para responder a los casos de uso del equipo Explore. No conoce
 * HTTP: la capa web es quien traduce esto a DTOs de respuesta.
 */
@Service
public class CatalogService {

  private static final int RECOMMENDATION_LIMIT = 4;

  private final ProductCatalog catalog;

  public CatalogService(ProductCatalog catalog) {
    this.catalog = catalog;
  }

  /** Teasers de categorías destacados para la home. Siempre las dos categorías conocidas. */
  public List<CategoryTeaser> homeTeasers() {
    return List.of(
        new CategoryTeaser(
            ProductCategory.CLASSIC, "Classic Tractors", "/images/teasers/classic-tractors.jpg"),
        new CategoryTeaser(
            ProductCategory.AUTONOMOUS,
            "Autonomous Tractors",
            "/images/teasers/autonomous-tractors.jpg"));
  }

  public List<Product> productsByCategory(ProductCategory category) {
    return catalog.byCategory(category);
  }

  public Optional<Product> findProduct(String productId) {
    return catalog.findById(productId);
  }

  public List<Store> stores() {
    return catalog.stores();
  }

  public List<RecommendedVariant> recommend(List<String> selectedSkus) {
    return catalog.recommend(selectedSkus, RECOMMENDATION_LIMIT).stream()
        .map(
            variant ->
                new RecommendedVariant(
                    variant, catalog.findByVariantSku(variant.sku()).orElseThrow()))
        .toList();
  }
}
