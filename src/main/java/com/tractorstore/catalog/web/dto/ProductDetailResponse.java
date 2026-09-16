package com.tractorstore.catalog.web.dto;

import com.tractorstore.catalog.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public record ProductDetailResponse(
    String id,
    String name,
    String description,
    String category,
    BigDecimal price,
    List<String> highlights,
    List<VariantResponse> variants) {

  public static ProductDetailResponse from(Product product) {
    return new ProductDetailResponse(
        product.id(),
        product.name(),
        product.description(),
        product.category().name().toLowerCase(Locale.ROOT),
        product.price(),
        product.highlights(),
        product.variants().stream().map(VariantResponse::from).toList());
  }
}
