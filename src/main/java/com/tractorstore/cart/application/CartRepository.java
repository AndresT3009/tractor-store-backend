package com.tractorstore.cart.application;

import com.tractorstore.cart.domain.Cart;

/** Puerto que la capa de aplicación usa para leer y guardar el carrito de una sesión. */
public interface CartRepository {

  Cart findBySessionId(String sessionId);

  void save(String sessionId, Cart cart);
}
