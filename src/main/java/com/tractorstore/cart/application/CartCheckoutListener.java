package com.tractorstore.cart.application;

import com.tractorstore.shared.events.OrderPlaced;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Vacía el carrito de la sesión una vez que el pedido queda confirmado.
 *
 * <p>Escucha {@link OrderPlaced} después de que la transacción que lo publicó (en {@code
 * order.application.OrderService}) hace commit, en vez de que sea Order quien llame directamente a
 * {@code CartService.clear} (Fase B10). Necesita {@code REQUIRES_NEW}: en fase {@code AFTER_COMMIT}
 * la transacción original ya cerró su conexión aunque Spring todavía la marca como "activa", así
 * que unirse a ella con la propagación por defecto falla con "No active transaction"; hace falta
 * forzar una transacción nueva.
 */
@Component
class CartCheckoutListener {

  private final CartService cartService;

  CartCheckoutListener(CartService cartService) {
    this.cartService = cartService;
  }

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void on(OrderPlaced event) {
    cartService.clear(event.sessionId());
  }
}
