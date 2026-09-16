package com.tractorstore.catalog.web.dto;

import com.tractorstore.catalog.application.RecommendedVariant;
import java.math.BigDecimal;

public record RecommendationResponse(
    String sku, String productId, String productName, BigDecimal price, String imageUrl) {

  public static RecommendationResponse from(RecommendedVariant recommended) {
    return new RecommendationResponse(
        recommended.variant().sku(),
        recommended.product().id(),
        recommended.product().name(),
        recommended.product().price(),
        recommended.variant().imageUrl());
  }
}
