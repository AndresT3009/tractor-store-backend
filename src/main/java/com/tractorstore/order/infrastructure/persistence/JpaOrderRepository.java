package com.tractorstore.order.infrastructure.persistence;

import com.tractorstore.order.application.OrderRepository;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.domain.OrderLine;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class JpaOrderRepository implements OrderRepository {

  private final OrderJpaRepository orders;

  JpaOrderRepository(OrderJpaRepository orders) {
    this.orders = orders;
  }

  @Override
  @Transactional
  public void save(Order order) {
    OrderEntity entity =
        new OrderEntity(
            order.id(), order.firstName(), order.lastName(), order.storeId(), order.placedAt());
    int position = 0;
    for (OrderLine line : order.lines()) {
      entity.addLine(
          new OrderLineEntity(position++, line.sku(), line.quantity(), line.unitPrice()));
    }
    orders.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> findById(String id) {
    return orders.findById(id).map(OrderEntity::toDomain);
  }
}
