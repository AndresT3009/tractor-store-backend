package com.tractorstore.cart.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.tractorstore.AbstractIntegrationTest;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaCartRepository.class)
class JpaCartRepositoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired private JpaCartRepository jpaCartRepository;

  @Test
  void should_returnEmptyCart_when_sessionHasNeverBeenSaved() {
    // Arrange & Act
    Cart result = jpaCartRepository.findBySessionId("unknown-session");

    // Assert
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void should_roundTripCartLines_forItsOwnSession() {
    // Arrange
    Cart cart =
        new Cart(
            List.of(new CartLineItem("SF-TITAN-COPPER", 2), new CartLineItem("RAPID-BLUE", 1)));

    // Act
    jpaCartRepository.save("session-a", cart);
    Cart result = jpaCartRepository.findBySessionId("session-a");

    // Assert
    assertThat(result.items()).containsExactlyInAnyOrderElementsOf(cart.items());
  }

  @Test
  void should_notMixLinesBetweenDifferentSessions() {
    // Arrange
    jpaCartRepository.save("session-b1", new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 1))));
    jpaCartRepository.save("session-b2", new Cart(List.of(new CartLineItem("RAPID-BLUE", 3))));

    // Act
    Cart result = jpaCartRepository.findBySessionId("session-b1");

    // Assert
    assertThat(result.items()).containsExactly(new CartLineItem("SF-TITAN-COPPER", 1));
  }

  @Test
  void should_replaceAllLines_when_savingOverAnExistingCart() {
    // Arrange
    jpaCartRepository.save("session-c", new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 5))));

    // Act
    jpaCartRepository.save("session-c", new Cart(List.of(new CartLineItem("RAPID-BLUE", 1))));
    Cart result = jpaCartRepository.findBySessionId("session-c");

    // Assert
    assertThat(result.items()).containsExactly(new CartLineItem("RAPID-BLUE", 1));
  }
}
