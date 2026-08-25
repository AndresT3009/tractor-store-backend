package com.tractorstore.inventory.infrastructure.persistence;

import com.tractorstore.inventory.application.InventoryRepository;
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
}
