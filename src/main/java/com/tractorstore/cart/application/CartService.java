package com.tractorstore.cart.application;

import com.tractorstore.cart.domain.Cart;
import org.springframework.stereotype.Service;

/**
 * Orquesta el carrito de una sesión. No sabe nada de HTTP, cookies ni PostgreSQL: recibe siempre un
 * {@code sessionId} ya resuelto por la capa web.
 */
@Service
public class CartService {

  private final CartRepository cartRepository;

  public CartService(CartRepository cartRepository) {
    this.cartRepository = cartRepository;
  }

  public Cart getCart(String sessionId) {
    return cartRepository.findBySessionId(sessionId);
  }

  public Cart addItem(String sessionId, String sku) {
    Cart updated = getCart(sessionId).withItemAdded(sku);
    cartRepository.save(sessionId, updated);
    return updated;
  }

  public Cart removeItem(String sessionId, String sku) {
    Cart updated = getCart(sessionId).withItemRemoved(sku);
    cartRepository.save(sessionId, updated);
    return updated;
  }

  /** Vacía el carrito. Lo usa el módulo order al confirmar un pedido. */
  public void clear(String sessionId) {
    cartRepository.save(sessionId, Cart.empty());
  }
}
