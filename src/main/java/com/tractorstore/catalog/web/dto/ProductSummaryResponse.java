package com.tractorstore.catalog.web.dto;

import com.tractorstore.catalog.domain.Product;
import java.math.BigDecimal;
import java.util.Locale;

public record ProductSummaryResponse(
    String id, String name, BigDecimal price, String imageUrl, String category) {

  public static ProductSummaryResponse from(Product product) {
    String imageUrl = product.variants().get(0).imageUrl();
    return new ProductSummaryResponse(
        product.id(),
        product.name(),
        product.price(),
        imageUrl,
        product.category().name().toLowerCase(Locale.ROOT));
  }
}
