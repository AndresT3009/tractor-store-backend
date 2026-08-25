package com.tractorstore.order.application;

import com.tractorstore.order.domain.Order;
import java.util.Optional;

/** Puerto que la capa de aplicación usa para guardar y recuperar pedidos confirmados. */
public interface OrderRepository {

  void save(Order order);

  Optional<Order> findById(String id);
}
