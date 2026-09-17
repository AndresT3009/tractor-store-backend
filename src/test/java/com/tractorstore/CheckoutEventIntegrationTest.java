package com.tractorstore;

import static org.assertj.core.api.Assertions.assertThat;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.order.application.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de punta a punta (Spring context real, Postgres real vía Testcontainers) de que el evento
 * {@code OrderPlaced} realmente vacía el carrito tras confirmar el pedido: order no llama a
 * cart.application.CartService.clear directamente, así que solo un test que dispare el commit de
 * transacción real puede verificar que el listener está conectado.
 *
 * <p>Usa COMMANDER-TEAL, no SF-TITAN-COPPER: esta compra descuenta stock real y de forma permanente
 * (a propósito, es justo lo que el test necesita verificar indirectamente vía el commit real), en
 * la misma base compartida por el resto de las clases de test (ver AbstractIntegrationTest) —
 * JpaInventoryRepositoryIntegrationTest verifica el valor exacto sembrado de SF-TITAN-COPPER, así
 * que comprarlo aquí lo dejaría en rojo según el orden en que corran las clases.
 */
@SpringBootTest
class CheckoutEventIntegrationTest extends AbstractIntegrationTest {

  private static final String SESSION_ID = "checkout-event-session";

  @Autowired private CartService cartService;
  @Autowired private OrderService orderService;

  @Test
  void should_clearCart_when_orderIsPlaced() {
    // Arrange
    cartService.addItem(SESSION_ID, "COMMANDER-TEAL");
    Cart cart = cartService.getCart(SESSION_ID);

    // Act
    orderService.placeOrder("Ada", "Lovelace", "aurora-flagship", cart, SESSION_ID);

    // Assert
    assertThat(cartService.getCart(SESSION_ID).isEmpty()).isTrue();
  }
}
