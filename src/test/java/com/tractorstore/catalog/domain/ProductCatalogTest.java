package com.tractorstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductCatalogTest {

  private static final Variant TITAN_ORANGE =
      new Variant("SF-TITAN-ORANGE", "Sunset Copper", "#E06010", "titan-orange.png");
  private static final Variant TITAN_BLUE =
      new Variant("SF-TITAN-BLUE", "Cosmic Sapphire", "#1040A0", "titan-blue.png");
  private static final Variant WORKHORSE_GREEN =
      new Variant("HERITAGE-GREEN", "Forest Green", "#E05010", "workhorse-green.png");
  private static final Variant RACER_BLUE =
      new Variant("RAPID-BLUE", "Racing Blue", "#1545A5", "racer-blue.png");

  private static final Product AUTONOMOUS_TITAN =
      new Product(
          "smartfarm-titan",
          "SmartFarm Titan",
          "Autonomous navigation across rough terrain",
          ProductCategory.AUTONOMOUS,
          new BigDecimal("4000.00"),
          List.of("Autonomous navigation", "Solar-assisted drivetrain"),
          List.of(TITAN_ORANGE, TITAN_BLUE));

  private static final Product CLASSIC_WORKHORSE =
      new Product(
          "heritage-workhorse",
          "Heritage Workhorse",
          "A dependable classic",
          ProductCategory.CLASSIC,
          new BigDecimal("5700.00"),
          List.of("Built to last"),
          List.of(WORKHORSE_GREEN));

  private static final Product CLASSIC_RACER =
      new Product(
          "rapid-racer",
          "Rapid Racer",
          "Fast and nimble",
          ProductCategory.CLASSIC,
          new BigDecimal("7500.00"),
          List.of("Lightweight frame"),
          List.of(RACER_BLUE));

  private final ProductCatalog catalog =
      new ProductCatalog(List.of(AUTONOMOUS_TITAN, CLASSIC_WORKHORSE, CLASSIC_RACER), List.of());

  @Test
  void should_returnOnlyMatchingCategory_when_categoryGiven() {
    // Arrange
    ProductCategory category = ProductCategory.CLASSIC;

    // Act
    List<Product> result = catalog.byCategory(category);

    // Assert
    assertThat(result).containsExactlyInAnyOrder(CLASSIC_WORKHORSE, CLASSIC_RACER);
  }

  @Test
  void should_returnEveryProduct_when_categoryIsNull() {
    // Arrange & Act
    List<Product> result = catalog.byCategory(null);

    // Assert
    assertThat(result).hasSize(3);
  }

  @Test
  void should_findProduct_when_idExists() {
    // Arrange & Act
    var result = catalog.findById("smartfarm-titan");

    // Assert
    assertThat(result).contains(AUTONOMOUS_TITAN);
  }

  @Test
  void should_returnEmpty_when_idDoesNotExist() {
    // Arrange & Act
    var result = catalog.findById("does-not-exist");

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  void should_recommendClosestColorFromOtherProducts_when_skuIsKnown() {
    // Arrange: TITAN_ORANGE (#E06010) is much closer to WORKHORSE_GREEN (#E05010)
    // than to RACER_BLUE (#1545A5), and its own product must be excluded.
    List<String> selectedSkus = List.of(TITAN_ORANGE.sku());

    // Act
    List<Variant> recommendations = catalog.recommend(selectedSkus, 2);

    // Assert
    assertThat(recommendations).containsExactly(WORKHORSE_GREEN, RACER_BLUE);
  }

  @Test
  void should_returnEmpty_when_noSelectedSkuIsKnown() {
    // Arrange
    List<String> unknownSkus = List.of("does-not-exist");

    // Act
    List<Variant> recommendations = catalog.recommend(unknownSkus, 4);

    // Assert
    assertThat(recommendations).isEmpty();
  }

  @Test
  void should_rejectNonPositiveLimit() {
    // Arrange
    List<String> selectedSkus = List.of(TITAN_ORANGE.sku());

    // Act & Assert
    assertThatIllegalArgumentException().isThrownBy(() -> catalog.recommend(selectedSkus, 0));
  }

  @Test
  void should_findOwningProduct_when_variantSkuExists() {
    // Arrange & Act
    var result = catalog.findByVariantSku(WORKHORSE_GREEN.sku());

    // Assert
    assertThat(result).contains(CLASSIC_WORKHORSE);
  }

  @Test
  void should_returnEmpty_when_variantSkuDoesNotExist() {
    // Arrange & Act
    var result = catalog.findByVariantSku("does-not-exist");

    // Assert
    assertThat(result).isEmpty();
  }
}
