package com.tractorstore.order.infrastructure.persistence;

import com.tractorstore.order.domain.OrderLine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Fila JPA de una línea de pedido. Nunca se expone fuera de infrastructure. */
@Entity
@Table(name = "order_line")
class OrderLineEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @Column(nullable = false)
  private int position;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private int quantity;

  @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;

  protected OrderLineEntity() {}

  OrderLineEntity(int position, String sku, int quantity, BigDecimal unitPrice) {
    this.position = position;
    this.sku = sku;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  void assignTo(OrderEntity order) {
    this.order = order;
  }

  OrderLine toDomain() {
    return new OrderLine(sku, quantity, unitPrice);
  }
}
