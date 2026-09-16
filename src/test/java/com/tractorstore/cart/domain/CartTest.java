package com.tractorstore.cart.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CartTest {

  @Test
  void should_beEmpty_when_justCreated() {
    // Arrange & Act
    Cart cart = Cart.empty();

    // Assert
    assertThat(cart.isEmpty()).isTrue();
    assertThat(cart.totalQuantity()).isZero();
  }

  @Test
  void should_addNewLine_when_skuNotYetInCart() {
    // Arrange
    Cart cart = Cart.empty();

    // Act
    Cart updated = cart.withItemAdded("SF-TITAN-ORANGE");

    // Assert
    assertThat(updated.items()).containsExactly(new CartLineItem("SF-TITAN-ORANGE", 1));
  }

  @Test
  void should_incrementQuantity_when_skuAlreadyInCart() {
    // Arrange
    Cart cart = Cart.empty().withItemAdded("SF-TITAN-ORANGE");

    // Act
    Cart updated = cart.withItemAdded("SF-TITAN-ORANGE");

    // Assert
    assertThat(updated.items()).containsExactly(new CartLineItem("SF-TITAN-ORANGE", 2));
  }

  @Test
  void should_removeLine_when_skuRequestedForRemoval() {
    // Arrange
    Cart cart = Cart.empty().withItemAdded("SF-TITAN-ORANGE").withItemAdded("HERITAGE-GREEN");

    // Act
    Cart updated = cart.withItemRemoved("SF-TITAN-ORANGE");

    // Assert
    assertThat(updated.items()).containsExactly(new CartLineItem("HERITAGE-GREEN", 1));
  }

  @Test
  void should_sumQuantitiesAcrossLines_when_computingTotalQuantity() {
    // Arrange
    Cart cart =
        Cart.empty()
            .withItemAdded("SF-TITAN-ORANGE")
            .withItemAdded("SF-TITAN-ORANGE")
            .withItemAdded("HERITAGE-GREEN");

    // Act
    int total = cart.totalQuantity();

    // Assert
    assertThat(total).isEqualTo(3);
  }
}
