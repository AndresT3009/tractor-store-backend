package com.tractorstore.cart.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * El carrito de compra de una sesión, como una colección inmutable de líneas.
 *
 * <p>Cada operación devuelve un nuevo {@code Cart}; no hay mutación interna. Esto refleja cómo se
 * va a usar desde la capa de aplicación: se lee el carrito de la sesión, se deriva uno nuevo, y se
 * vuelve a guardar.
 */
public record Cart(List<CartLineItem> items) {

  public Cart {
    items = List.copyOf(items);
  }

  public static Cart empty() {
    return new Cart(List.of());
  }

  /** Añade una unidad de {@code sku}; si ya está en el carrito, incrementa su cantidad. */
  public Cart withItemAdded(String sku) {
    List<CartLineItem> updated = new ArrayList<>();
    boolean found = false;
    for (CartLineItem item : items) {
      if (item.sku().equals(sku)) {
        updated.add(item.withQuantity(item.quantity() + 1));
        found = true;
      } else {
        updated.add(item);
      }
    }
    if (!found) {
      updated.add(new CartLineItem(sku, 1));
    }
    return new Cart(updated);
  }

  /** Elimina por completo la línea de {@code sku}, sin importar su cantidad. */
  public Cart withItemRemoved(String sku) {
    return new Cart(items.stream().filter(item -> !item.sku().equals(sku)).toList());
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  public int totalQuantity() {
    return items.stream().mapToInt(CartLineItem::quantity).sum();
  }
}
