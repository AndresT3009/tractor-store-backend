package com.tractorstore.inventory.domain;

/** Se intentó descontar más unidades de un SKU que las disponibles. */
public class InsufficientStockException extends RuntimeException {

  private final String sku;

  public InsufficientStockException(String sku) {
    super("Sin stock suficiente para " + sku);
    this.sku = sku;
  }

  public String sku() {
    return sku;
  }
}
