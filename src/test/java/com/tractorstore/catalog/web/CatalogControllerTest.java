package com.tractorstore.catalog.web;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.application.CategoryTeaser;
import com.tractorstore.catalog.application.RecommendedVariant;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.domain.Store;
import com.tractorstore.catalog.domain.Variant;
import com.tractorstore.shared.web.SecurityConfig;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(CatalogController.class)
@Import(SecurityConfig.class)
class CatalogControllerTest {

  private static final Variant TITAN_ORANGE =
      new Variant("SF-TITAN-ORANGE", "Sunset Copper", "#E06010", "titan-orange.png");
  private static final Product AUTONOMOUS_TITAN =
      new Product(
          "smartfarm-titan",
          "SmartFarm Titan",
          "Autonomous navigation across rough terrain",
          ProductCategory.AUTONOMOUS,
          new BigDecimal("4000.00"),
          List.of("Autonomous navigation"),
          List.of(TITAN_ORANGE));

  @Autowired private MockMvcTester mvc;

  @MockitoBean private CatalogService catalogService;

  @Test
  void should_returnHomeTeasers() {
    // Arrange
    given(catalogService.homeTeasers())
        .willReturn(
            List.of(
                new CategoryTeaser(ProductCategory.CLASSIC, "Classic Tractors", "/classic.jpg")));

    // Act & Assert
    mvc.get()
        .uri("/api/catalog/home")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.categories[0].category")
        .isEqualTo("classic");
  }

  @Test
  void should_returnFilteredProducts_when_categoryFilterIsValid() {
    // Arrange
    given(catalogService.productsByCategory(eq(ProductCategory.AUTONOMOUS)))
        .willReturn(List.of(AUTONOMOUS_TITAN));

    // Act & Assert
    mvc.get()
        .uri("/api/catalog/categories/{filter}", "autonomous")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.products[0].id")
        .isEqualTo("smartfarm-titan");
  }

  @Test
  void should_passNullCategory_when_filterIsAll() {
    // Arrange
    given(catalogService.productsByCategory(isNull())).willReturn(List.of(AUTONOMOUS_TITAN));

    // Act & Assert
    mvc.get().uri("/api/catalog/categories/{filter}", "all").assertThat().hasStatusOk();
  }

  @Test
  void should_rejectUnknownCategoryFilter() {
    // Arrange, Act & Assert
    mvc.get().uri("/api/catalog/categories/{filter}", "not-a-category").assertThat().hasStatus(400);
  }

  @Test
  void should_returnProductDetail_when_idExists() {
    // Arrange
    given(catalogService.findProduct("smartfarm-titan")).willReturn(Optional.of(AUTONOMOUS_TITAN));

    // Act & Assert
    mvc.get()
        .uri("/api/catalog/products/{id}", "smartfarm-titan")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.name")
        .isEqualTo("SmartFarm Titan");
  }

  @Test
  void should_return404_when_productIdDoesNotExist() {
    // Arrange
    given(catalogService.findProduct("unknown")).willReturn(Optional.empty());

    // Act & Assert
    mvc.get().uri("/api/catalog/products/{id}", "unknown").assertThat().hasStatus(404);
  }

  @Test
  void should_returnRecommendations_when_skusGiven() {
    // Arrange
    given(catalogService.recommend(List.of("SF-TITAN-ORANGE")))
        .willReturn(List.of(new RecommendedVariant(TITAN_ORANGE, AUTONOMOUS_TITAN)));

    // Act & Assert
    mvc.get()
        .uri("/api/catalog/recommendations?skus=SF-TITAN-ORANGE")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$[0].sku")
        .isEqualTo("SF-TITAN-ORANGE");
  }

  @Test
  void should_returnStores() {
    // Arrange
    given(catalogService.stores())
        .willReturn(
            List.of(new Store("aurora", "Aurora Flagship", "Astronaut Way 1", "Arlington")));

    // Act & Assert
    mvc.get()
        .uri("/api/catalog/stores")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$[0].city")
        .isEqualTo("Arlington");
  }
}
