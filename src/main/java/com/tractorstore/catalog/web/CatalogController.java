package com.tractorstore.catalog.web;

import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.web.dto.CategoryResponse;
import com.tractorstore.catalog.web.dto.CategoryTeaserResponse;
import com.tractorstore.catalog.web.dto.HomeResponse;
import com.tractorstore.catalog.web.dto.ProductDetailResponse;
import com.tractorstore.catalog.web.dto.ProductSummaryResponse;
import com.tractorstore.catalog.web.dto.RecommendationResponse;
import com.tractorstore.catalog.web.dto.StoreResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoints del módulo Catalog, consumidos por mfe-explore y mfe-decide. */
@RestController
@RequestMapping("/api/catalog")
class CatalogController {

  private static final List<String> AVAILABLE_FILTERS = List.of("all", "classic", "autonomous");

  private final CatalogService catalogService;

  CatalogController(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @GetMapping("/home")
  HomeResponse home() {
    List<CategoryTeaserResponse> teasers =
        catalogService.homeTeasers().stream().map(CategoryTeaserResponse::from).toList();
    return new HomeResponse(teasers);
  }

  @GetMapping("/categories/{filter}")
  CategoryResponse categories(@PathVariable String filter) {
    ProductCategory category = parseFilter(filter);
    List<ProductSummaryResponse> products =
        catalogService.productsByCategory(category).stream()
            .map(ProductSummaryResponse::from)
            .toList();
    return new CategoryResponse(products, AVAILABLE_FILTERS);
  }

  @GetMapping("/products/{id}")
  ProductDetailResponse product(@PathVariable String id) {
    return catalogService
        .findProduct(id)
        .map(ProductDetailResponse::from)
        .orElseThrow(
            () ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
  }

  @GetMapping("/recommendations")
  List<RecommendationResponse> recommendations(
      @RequestParam(name = "skus", required = false, defaultValue = "") String skus) {
    return catalogService.recommend(parseSkus(skus)).stream()
        .map(RecommendationResponse::from)
        .toList();
  }

  @GetMapping("/stores")
  List<StoreResponse> stores() {
    return catalogService.stores().stream().map(StoreResponse::from).toList();
  }

  private ProductCategory parseFilter(String filter) {
    if ("all".equalsIgnoreCase(filter)) {
      return null;
    }
    try {
      return ProductCategory.valueOf(filter.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Filtro de categoría desconocido: " + filter);
    }
  }

  private List<String> parseSkus(String csv) {
    if (csv == null || csv.isBlank()) {
      return List.of();
    }
    return Arrays.stream(csv.split(",")).map(String::trim).filter(sku -> !sku.isBlank()).toList();
  }
}
