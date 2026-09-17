package com.tractorstore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface StockJpaRepository extends JpaRepository<StockEntity, String> {

  // UPDATE condicional: la fila solo se toca si ya tiene suficiente stock, así que la
  // atomicidad la garantiza la propia base de datos (no una lectura-y-luego-escritura desde
  // Java, que sí sería vulnerable a una condición de carrera entre dos compras concurrentes).
  @Modifying
  @Query(
      "UPDATE StockEntity s SET s.quantityAvailable = s.quantityAvailable - :quantity "
          + "WHERE s.sku = :sku AND s.quantityAvailable >= :quantity")
  int decrementIfAvailable(@Param("sku") String sku, @Param("quantity") int quantity);
}
