package com.tractorstore.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCatalog;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.domain.Store;
import com.tractorstore.catalog.domain.Variant;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

  private static final Variant TITAN_ORANGE =
      new Variant("SF-TITAN-ORANGE", "Sunset Copper", "#E06010", "titan-orange.png");
  private static final Variant WORKHORSE_GREEN =
      new Variant("HERITAGE-GREEN", "Forest Green", "#E05010", "workhorse-green.png");

  private static final Product AUTONOMOUS_TITAN =
      new Product(
          "smartfarm-titan",
          "SmartFarm Titan",
          "Autonomous navigation across rough terrain",
          ProductCategory.AUTONOMOUS,
          new BigDecimal("4000.00"),
          List.of("Autonomous navigation"),
          List.of(TITAN_ORANGE));

  private static final Product CLASSIC_WORKHORSE =
      new Product(
          "heritage-workhorse",
          "Heritage Workhorse",
          "A dependable classic",
          ProductCategory.CLASSIC,
          new BigDecimal("5700.00"),
          List.of("Built to last"),
          List.of(WORKHORSE_GREEN));

  private static final Store AURORA =
      new Store(
          "aurora", "Aurora Flagship", "Astronaut Way 1", "Arlington", "/images/stores/aurora.jpg");

  @Mock private CatalogRepository catalogRepository;

  private CatalogService catalogService;

  @BeforeEach
  void setUp() {
    catalogService = new CatalogService(catalogRepository);
    lenient()
        .when(catalogRepository.load())
        .thenReturn(
            new ProductCatalog(List.of(AUTONOMOUS_TITAN, CLASSIC_WORKHORSE), List.of(AURORA)));
  }

  @Test
  void should_exposeBothKnownCategories_when_buildingHomeTeasers() {
    // Arrange & Act
    List<CategoryTeaser> teasers = catalogService.homeTeasers();

    // Assert
    assertThat(teasers)
        .extracting(CategoryTeaser::category)
        .containsExactlyInAnyOrder(ProductCategory.CLASSIC, ProductCategory.AUTONOMOUS);
  }

  @Test
  void should_delegateToCatalog_when_filteringByCategory() {
    // Arrange & Act
    List<Product> result = catalogService.productsByCategory(ProductCategory.CLASSIC);

    // Assert
    assertThat(result).containsExactly(CLASSIC_WORKHORSE);
  }

  @Test
  void should_findProduct_when_idExists() {
    // Arrange & Act
    var result = catalogService.findProduct("smartfarm-titan");

    // Assert
    assertThat(result).contains(AUTONOMOUS_TITAN);
  }

  @Test
  void should_returnConfiguredStores() {
    // Arrange & Act
    List<Store> result = catalogService.stores();

    // Assert
    assertThat(result).containsExactly(AURORA);
  }

  @Test
  void should_findOwningProduct_when_variantSkuIsKnown() {
    // Arrange & Act
    var result = catalogService.findProductByVariantSku(WORKHORSE_GREEN.sku());

    // Assert
    assertThat(result).contains(CLASSIC_WORKHORSE);
  }

  @Test
  void should_pairRecommendedVariantWithItsOwningProduct() {
    // Arrange
    List<String> selectedSkus = List.of(TITAN_ORANGE.sku());

    // Act
    List<RecommendedVariant> recommendations = catalogService.recommend(selectedSkus);

    // Assert
    assertThat(recommendations)
        .containsExactly(new RecommendedVariant(WORKHORSE_GREEN, CLASSIC_WORKHORSE));
  }
}
