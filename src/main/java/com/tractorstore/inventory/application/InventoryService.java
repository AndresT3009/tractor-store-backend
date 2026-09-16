package com.tractorstore.inventory.application;

import com.tractorstore.inventory.domain.StockLevel;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** Orquesta {@link InventoryRepository} para responder al caso de uso del equipo Decide. */
@Service
public class InventoryService {

  private final InventoryRepository inventoryRepository;

  public InventoryService(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  public Optional<StockLevel> stockOf(String sku) {
    return inventoryRepository.findBySku(sku);
  }
}
