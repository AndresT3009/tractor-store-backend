package com.tractorstore.catalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.tractorstore.AbstractIntegrationTest;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCatalog;
import com.tractorstore.catalog.domain.Variant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

/**
 * Verifica el viaje completo: migraciones Flyway (V1-V2) + mapeo JPA + conversión a dominio, contra
 * un PostgreSQL real de Testcontainers. {@code Replace.NONE} evita que Spring sustituya la base de
 * Testcontainers por una H2 en memoria, que es el comportamiento por defecto de
 * {@code @DataJpaTest}.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaCatalogRepository.class)
class JpaCatalogRepositoryIntegrationTest extends AbstractIntegrationTest {

  @Autowired private JpaCatalogRepository jpaCatalogRepository;

  @Test
  void should_loadEverySeededProductAndStore() {
    // Arrange & Act
    ProductCatalog catalog = jpaCatalogRepository.load();

    // Assert
    assertThat(catalog.byCategory(null)).hasSize(5);
    assertThat(catalog.stores()).hasSize(4);
  }

  @Test
  void should_mapHighlightsAndVariantsInSeedOrder() {
    // Arrange & Act
    Optional<Product> result = jpaCatalogRepository.load().findById("smartfarm-titan");

    // Assert
    assertThat(result).isPresent();
    Product product = result.orElseThrow();
    assertThat(product.name()).isEqualTo("SmartFarm Titan");
    assertThat(product.price()).isEqualByComparingTo("4000.00");
    assertThat(product.highlights())
        .containsExactly(
            "Autonomous navigation across rough terrain",
            "Solar-assisted drivetrain for all-day work",
            "Modular tool bay for field-specific attachments");
    assertThat(product.variants())
        .extracting(Variant::sku)
        .containsExactly("SF-TITAN-COPPER", "SF-TITAN-SAPPHIRE");
  }

  @Test
  void should_returnEmpty_when_productIdIsUnknown() {
    // Arrange & Act
    Optional<Product> result = jpaCatalogRepository.load().findById("does-not-exist");

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  void should_recommendUsingRealPersistedColors() {
    // Arrange: SF-TITAN-COPPER (#C24914) is closest to HERITAGE-GREEN (#4C7A2E) among the
    // other products' variants once its own product (smartfarm-titan) is excluded.
    List<String> selectedSkus = List.of("SF-TITAN-COPPER");

    // Act
    List<Variant> recommendations = jpaCatalogRepository.load().recommend(selectedSkus, 1);

    // Assert
    assertThat(recommendations).extracting(Variant::sku).containsExactly("HERITAGE-GREEN");
  }
}
