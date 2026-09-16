package com.tractorstore.inventory.application;

import com.tractorstore.inventory.domain.StockLevel;
import java.util.Optional;

/** Puerto que la capa de aplicación usa para consultar stock, sin saber de dónde viene. */
public interface InventoryRepository {

  Optional<StockLevel> findBySku(String sku);
}
