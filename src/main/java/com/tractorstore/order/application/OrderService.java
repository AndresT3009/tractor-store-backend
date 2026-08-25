package com.tractorstore.order.application;

import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.domain.OrderLine;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Confirma pedidos a partir de un carrito ya resuelto por la capa web, y los recupera por id.
 *
 * <p>Recibe el {@link Cart} como parámetro en vez de depender de {@code cart.application}
 * directamente: no necesita saber nada de sesiones ni de cómo se persiste el carrito, solo qué
 * líneas comprar. La orquestación completa del checkout (leer carrito → crear pedido → vaciar
 * carrito) vive por ahora en {@code OrderController}; en la Fase B10 se reemplaza por un evento de
 * dominio {@code CheckoutRequested} publicado por Cart y escuchado por Order.
 */
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final CatalogService catalogService;
  private final Clock clock;

  public OrderService(OrderRepository orderRepository, CatalogService catalogService, Clock clock) {
    this.orderRepository = orderRepository;
    this.catalogService = catalogService;
    this.clock = clock;
  }

  public Order placeOrder(String firstName, String lastName, String storeId, Cart cart) {
    List<OrderLine> lines = cart.items().stream().map(this::toOrderLine).toList();
    Order order =
        new Order(
            UUID.randomUUID().toString(), firstName, lastName, storeId, lines, Instant.now(clock));
    orderRepository.save(order);
    return order;
  }

  public Optional<Order> findById(String id) {
    return orderRepository.findById(id);
  }

  private OrderLine toOrderLine(CartLineItem item) {
    Product product =
        catalogService
            .findProductByVariantSku(item.sku())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "SKU en el carrito ya no existe en el catálogo: " + item.sku()));
    return new OrderLine(item.sku(), item.quantity(), product.price());
  }
}
