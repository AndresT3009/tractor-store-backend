package com.tractorstore.order.application;

import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.domain.OrderLine;
import com.tractorstore.shared.events.OrderPlaced;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Confirma pedidos a partir de un carrito ya resuelto por la capa web, y los recupera por id.
 *
 * <p>Recibe el {@link Cart} como parámetro en vez de depender de {@code cart.application}
 * directamente: no necesita saber nada de sesiones ni de cómo se persiste el carrito, solo qué
 * líneas comprar. Al confirmar el pedido publica {@link OrderPlaced}; es el módulo cart el que
 * escucha ese evento (vía {@code @TransactionalEventListener(AFTER_COMMIT)}) para vaciar el carrito
 * de la sesión, en vez de que {@code OrderController} llame a {@code CartService.clear}
 * directamente (Fase B10).
 */
@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final CatalogService catalogService;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  public OrderService(
      OrderRepository orderRepository,
      CatalogService catalogService,
      ApplicationEventPublisher eventPublisher,
      Clock clock) {
    this.orderRepository = orderRepository;
    this.catalogService = catalogService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Order placeOrder(
      String firstName, String lastName, String storeId, Cart cart, String sessionId) {
    List<OrderLine> lines = cart.items().stream().map(this::toOrderLine).toList();
    Order order =
        new Order(
            UUID.randomUUID().toString(), firstName, lastName, storeId, lines, Instant.now(clock));
    orderRepository.save(order);
    eventPublisher.publishEvent(new OrderPlaced(order.id(), sessionId));
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
