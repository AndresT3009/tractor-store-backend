package com.tractorstore.inventory.web.dto;

import com.tractorstore.inventory.domain.StockLevel;

public record StockResponse(String sku, int quantityAvailable, boolean available) {

  public static StockResponse from(StockLevel stockLevel) {
    return new StockResponse(
        stockLevel.sku(), stockLevel.quantityAvailable(), stockLevel.isAvailable());
  }
}
