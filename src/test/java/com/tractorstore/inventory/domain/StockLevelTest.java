package com.tractorstore.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class StockLevelTest {

  @Test
  void should_beAvailable_when_quantityIsPositive() {
    // Arrange
    StockLevel stock = new StockLevel("SF-TITAN-ORANGE", 6);

    // Act & Assert
    assertThat(stock.isAvailable()).isTrue();
  }

  @Test
  void should_notBeAvailable_when_quantityIsZero() {
    // Arrange
    StockLevel stock = new StockLevel("SF-TITAN-ORANGE", 0);

    // Act & Assert
    assertThat(stock.isAvailable()).isFalse();
  }

  @Test
  void should_rejectNegativeQuantity() {
    // Arrange & Act & Assert
    assertThatIllegalArgumentException().isThrownBy(() -> new StockLevel("SF-TITAN-ORANGE", -1));
  }
}
