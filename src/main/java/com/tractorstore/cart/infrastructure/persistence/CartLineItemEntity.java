package com.tractorstore.cart.infrastructure.persistence;

import com.tractorstore.cart.domain.CartLineItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila JPA de una línea de carrito. Nunca se expone fuera de infrastructure. */
@Entity
@Table(name = "cart_line_item")
class CartLineItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "session_id", nullable = false)
  private String sessionId;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private int quantity;

  protected CartLineItemEntity() {}

  CartLineItemEntity(String sessionId, String sku, int quantity) {
    this.sessionId = sessionId;
    this.sku = sku;
    this.quantity = quantity;
  }

  CartLineItem toDomain() {
    return new CartLineItem(sku, quantity);
  }
}
