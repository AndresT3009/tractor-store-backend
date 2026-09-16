package com.tractorstore.catalog.web.dto;

import com.tractorstore.catalog.domain.Variant;

public record VariantResponse(String sku, String colorName, String colorHex, String imageUrl) {

  public static VariantResponse from(Variant variant) {
    return new VariantResponse(
        variant.sku(), variant.colorName(), variant.colorHex(), variant.imageUrl());
  }
}
