package com.tractorstore.cart.infrastructure.persistence;

import com.tractorstore.cart.application.CartRepository;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persiste el carrito como una fila por línea, sin una tabla de "carrito" separada: un carrito no
 * tiene más atributos que sus líneas. {@code save} reemplaza todas las líneas de la sesión en vez
 * de calcular un diff: los carritos son pequeños y esto evita lógica de upsert innecesaria.
 */
@Component
class JpaCartRepository implements CartRepository {

  private final CartLineItemJpaRepository lines;

  JpaCartRepository(CartLineItemJpaRepository lines) {
    this.lines = lines;
  }

  @Override
  @Transactional(readOnly = true)
  public Cart findBySessionId(String sessionId) {
    List<CartLineItem> items =
        lines.findBySessionId(sessionId).stream().map(CartLineItemEntity::toDomain).toList();
    return new Cart(items);
  }

  @Override
  @Transactional
  public void save(String sessionId, Cart cart) {
    lines.deleteBySessionId(sessionId);
    lines.flush();
    List<CartLineItemEntity> entities =
        cart.items().stream()
            .map(item -> new CartLineItemEntity(sessionId, item.sku(), item.quantity()))
            .toList();
    lines.saveAll(entities);
  }
}
