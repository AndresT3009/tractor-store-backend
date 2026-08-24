package com.tractorstore.order.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void should_sumEveryLineSubtotal_when_computingTotalPrice() {
    // Arrange
    Order order =
        new Order(
            "order-1",
            "Ada",
            "Lovelace",
            "store-aurora",
            List.of(
                new OrderLine("SF-TITAN-ORANGE", 2, new BigDecimal("4000.00")),
                new OrderLine("HERITAGE-GREEN", 1, new BigDecimal("5700.00"))),
            Instant.parse("2026-08-24T10:00:00Z"));

    // Act
    BigDecimal total = order.totalPrice();

    // Assert
    assertThat(total).isEqualByComparingTo("13700.00");
  }

  @Test
  void should_rejectOrder_when_itHasNoLines() {
    // Arrange & Act & Assert
    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                new Order("order-1", "Ada", "Lovelace", "store-aurora", List.of(), Instant.now()));
  }
}
