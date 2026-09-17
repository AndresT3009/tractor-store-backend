package com.tractorstore.inventory.infrastructure.persistence;

import com.tractorstore.inventory.application.InventoryRepository;
import com.tractorstore.inventory.domain.InsufficientStockException;
import com.tractorstore.inventory.domain.StockLevel;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class JpaInventoryRepository implements InventoryRepository {

  private final StockJpaRepository stock;

  JpaInventoryRepository(StockJpaRepository stock) {
    this.stock = stock;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StockLevel> findBySku(String sku) {
    return stock.findById(sku).map(StockEntity::toDomain);
  }

  @Override
  @Transactional
  public void decrement(String sku, int quantity) {
    int updatedRows = stock.decrementIfAvailable(sku, quantity);
    if (updatedRows == 0) {
      throw new InsufficientStockException(sku);
    }
  }
}
