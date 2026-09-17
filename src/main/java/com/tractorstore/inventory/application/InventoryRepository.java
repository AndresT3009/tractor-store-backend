package com.tractorstore.inventory.application;

import com.tractorstore.inventory.domain.InsufficientStockException;
import com.tractorstore.inventory.domain.StockLevel;
import java.util.Optional;

/**
 * Puerto que la capa de aplicación usa para consultar y descontar stock, sin saber de dónde viene.
 */
public interface InventoryRepository {

  Optional<StockLevel> findBySku(String sku);

  /**
   * Descuenta {@code quantity} unidades de {@code sku} de forma atómica (a nivel de fila de base de
   * datos, no de aplicación) para que dos compras concurrentes del mismo SKU no puedan dejar el
   * stock en negativo.
   *
   * @throws InsufficientStockException si no hay {@code quantity} unidades disponibles
   */
  void decrement(String sku, int quantity);
}
