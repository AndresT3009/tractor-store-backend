package com.tractorstore.order.infrastructure.persistence;

import com.tractorstore.order.domain.Order;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.BatchSize;

/** Fila JPA de un pedido. Nunca se expone fuera de infrastructure: ver {@link #toDomain()}. */
@Entity
@Table(name = "order_order")
class OrderEntity {

  @Id private String id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "store_id", nullable = false)
  private String storeId;

  @Column(name = "placed_at", nullable = false)
  private Instant placedAt;

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  @BatchSize(size = 20)
  private List<OrderLineEntity> lines = new ArrayList<>();

  protected OrderEntity() {}

  OrderEntity(String id, String firstName, String lastName, String storeId, Instant placedAt) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.storeId = storeId;
    this.placedAt = placedAt;
  }

  void addLine(OrderLineEntity line) {
    line.assignTo(this);
    lines.add(line);
  }

  Order toDomain() {
    return new Order(
        id,
        firstName,
        lastName,
        storeId,
        lines.stream().map(OrderLineEntity::toDomain).toList(),
        placedAt);
  }
}
