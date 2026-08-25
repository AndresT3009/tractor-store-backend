package com.tractorstore.cart.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  private static final String SESSION_ID = "session-1";

  @Mock private CartRepository cartRepository;

  private CartService cartService;

  @Test
  void should_returnStoredCart_when_gettingIt() {
    // Arrange
    cartService = new CartService(cartRepository);
    Cart stored = Cart.empty().withItemAdded("SF-TITAN-COPPER");
    given(cartRepository.findBySessionId(SESSION_ID)).willReturn(stored);

    // Act
    Cart result = cartService.getCart(SESSION_ID);

    // Assert
    assertThat(result).isEqualTo(stored);
  }

  @Test
  void should_addItemAndPersistUpdatedCart() {
    // Arrange
    cartService = new CartService(cartRepository);
    given(cartRepository.findBySessionId(SESSION_ID)).willReturn(Cart.empty());

    // Act
    Cart result = cartService.addItem(SESSION_ID, "SF-TITAN-COPPER");

    // Assert
    assertThat(result.items()).containsExactly(new CartLineItem("SF-TITAN-COPPER", 1));
    verify(cartRepository).save(eq(SESSION_ID), eq(result));
  }

  @Test
  void should_removeItemAndPersistUpdatedCart() {
    // Arrange
    cartService = new CartService(cartRepository);
    Cart existing = Cart.empty().withItemAdded("SF-TITAN-COPPER");
    given(cartRepository.findBySessionId(SESSION_ID)).willReturn(existing);

    // Act
    Cart result = cartService.removeItem(SESSION_ID, "SF-TITAN-COPPER");

    // Assert
    assertThat(result.isEmpty()).isTrue();
    verify(cartRepository).save(SESSION_ID, result);
  }

  @Test
  void should_persistEmptyCart_when_clearing() {
    // Arrange
    cartService = new CartService(cartRepository);

    // Act
    cartService.clear(SESSION_ID);

    // Assert
    verify(cartRepository).save(SESSION_ID, Cart.empty());
  }
}
