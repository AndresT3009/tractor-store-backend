package com.tractorstore.inventory.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.tractorstore.AbstractIntegrationTest;
import com.tractorstore.inventory.domain.StockLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaInventoryRepository.class)
class JpaInventoryRepositoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired private JpaInventoryRepository jpaInventoryRepository;

  @Test
  void should_findSeededStock_when_skuExists() {
    // Arrange & Act
    var result = jpaInventoryRepository.findBySku("SF-TITAN-COPPER");

    // Assert
    assertThat(result).contains(new StockLevel("SF-TITAN-COPPER", 6));
  }

  @Test
  void should_reportOutOfStock_when_seededQuantityIsZero() {
    // Arrange & Act
    var result = jpaInventoryRepository.findBySku("RAPID-BLUE");

    // Assert
    assertThat(result).isPresent();
    assertThat(result.orElseThrow().isAvailable()).isFalse();
  }

  @Test
  void should_returnEmpty_when_skuIsUnknown() {
    // Arrange & Act
    var result = jpaInventoryRepository.findBySku("does-not-exist");

    // Assert
    assertThat(result).isEmpty();
  }
}
