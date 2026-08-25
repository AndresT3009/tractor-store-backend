package com.tractorstore.inventory.infrastructure.persistence;

import com.tractorstore.inventory.domain.StockLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila JPA de stock por SKU. Nunca se expone fuera de infrastructure: ver {@link #toDomain()}. */
@Entity
@Table(name = "inventory_stock")
class StockEntity {

  @Id private String sku;

  @Column(name = "quantity_available", nullable = false)
  private int quantityAvailable;

  protected StockEntity() {}

  StockEntity(String sku, int quantityAvailable) {
    this.sku = sku;
    this.quantityAvailable = quantityAvailable;
  }

  StockLevel toDomain() {
    return new StockLevel(sku, quantityAvailable);
  }
}
