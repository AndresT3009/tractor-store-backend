package com.tractorstore.order.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.tractorstore.AbstractIntegrationTest;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.domain.OrderLine;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaOrderRepository.class)
class JpaOrderRepositoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired private JpaOrderRepository jpaOrderRepository;

  @Test
  void should_roundTripOrderWithItsLinesInOrder() {
    // Arrange
    Order order =
        new Order(
            "order-1",
            "Ada",
            "Lovelace",
            "aurora-flagship",
            List.of(
                new OrderLine("SF-TITAN-COPPER", 2, new BigDecimal("4000.00")),
                new OrderLine("HERITAGE-GREEN", 1, new BigDecimal("5700.00"))),
            Instant.parse("2026-08-25T10:00:00Z"));

    // Act
    jpaOrderRepository.save(order);
    var result = jpaOrderRepository.findById("order-1");

    // Assert
    assertThat(result).isPresent();
    Order loaded = result.orElseThrow();
    assertThat(loaded.firstName()).isEqualTo("Ada");
    assertThat(loaded.placedAt()).isEqualTo(Instant.parse("2026-08-25T10:00:00Z"));
    assertThat(loaded.lines())
        .extracting(OrderLine::sku)
        .containsExactly("SF-TITAN-COPPER", "HERITAGE-GREEN");
    assertThat(loaded.totalPrice()).isEqualByComparingTo("13700.00");
  }

  @Test
  void should_returnEmpty_when_orderIdIsUnknown() {
    // Arrange & Act
    var result = jpaOrderRepository.findById("does-not-exist");

    // Assert
    assertThat(result).isEmpty();
  }
}
