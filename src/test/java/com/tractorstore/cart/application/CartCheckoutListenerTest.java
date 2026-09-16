package com.tractorstore.cart.application;

import static org.mockito.Mockito.verify;

import com.tractorstore.shared.events.OrderPlaced;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartCheckoutListenerTest {

  @Mock private CartService cartService;

  @Test
  void should_clearCart_when_orderPlacedForSession() {
    // Arrange
    var listener = new CartCheckoutListener(cartService);

    // Act
    listener.on(new OrderPlaced("order-1", "session-1"));

    // Assert
    verify(cartService).clear("session-1");
  }
}
