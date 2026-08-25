package com.tractorstore.inventory.web;

import com.tractorstore.inventory.application.InventoryService;
import com.tractorstore.inventory.web.dto.StockResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoint del módulo Inventory, consumido por mfe-decide. */
@RestController
@RequestMapping("/api/inventory")
class InventoryController {

  private final InventoryService inventoryService;

  InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping("/{sku}")
  StockResponse stockOf(@PathVariable String sku) {
    return inventoryService
        .stockOf(sku)
        .map(StockResponse::from)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "SKU no encontrado en inventario: " + sku));
  }
}
