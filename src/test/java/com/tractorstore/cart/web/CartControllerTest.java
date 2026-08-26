package com.tractorstore.cart.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.domain.Variant;
import com.tractorstore.shared.web.SecurityConfig;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
class CartControllerTest {

  private static final Variant TITAN_COPPER =
      new Variant("SF-TITAN-COPPER", "Sunset Copper", "#C24914", "/images/titan-copper.png");
  private static final Product SMARTFARM_TITAN =
      new Product(
          "smartfarm-titan",
          "SmartFarm Titan",
          "Autonomous navigation",
          ProductCategory.AUTONOMOUS,
          new BigDecimal("4000.00"),
          List.of("Highlight"),
          List.of(TITAN_COPPER));

  @Autowired private MockMvcTester mvc;

  @MockitoBean private CartService cartService;
  @MockitoBean private CatalogService catalogService;

  @Test
  void should_returnEnrichedCart_when_gettingCurrentCart() {
    // Arrange
    Cart cart = new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 2)));
    given(cartService.getCart(anyString())).willReturn(cart);
    given(catalogService.findProductByVariantSku("SF-TITAN-COPPER"))
        .willReturn(Optional.of(SMARTFARM_TITAN));

    // Act & Assert
    mvc.get()
        .uri("/api/cart")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.items[0].productName")
        .isEqualTo("SmartFarm Titan");
  }

  @Test
  void should_return409_when_cartLineSkuNoLongerInCatalog() {
    // Arrange
    Cart cart = new Cart(List.of(new CartLineItem("GHOST-SKU", 1)));
    given(cartService.getCart(anyString())).willReturn(cart);
    given(catalogService.findProductByVariantSku("GHOST-SKU")).willReturn(Optional.empty());

    // Act & Assert
    mvc.get().uri("/api/cart").assertThat().hasStatus(409);
  }

  @Test
  void should_returnTotalQuantityOnly_when_gettingMiniCart() {
    // Arrange
    Cart cart = new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 3)));
    given(cartService.getCart(anyString())).willReturn(cart);

    // Act & Assert
    mvc.get()
        .uri("/api/cart/mini")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.totalQuantity")
        .isEqualTo(3);
  }

  @Test
  void should_addItem_when_postingToItems() {
    // Arrange
    Cart updated = new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 1)));
    given(cartService.addItem(anyString(), eq("SF-TITAN-COPPER"))).willReturn(updated);
    given(catalogService.findProductByVariantSku("SF-TITAN-COPPER"))
        .willReturn(Optional.of(SMARTFARM_TITAN));

    // Act & Assert
    mvc.post()
        .uri("/api/cart/items")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"sku\":\"SF-TITAN-COPPER\"}")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.totalQuantity")
        .isEqualTo(1);
  }

  @Test
  void should_removeItem_when_deletingFromItems() {
    // Arrange
    given(cartService.removeItem(anyString(), eq("SF-TITAN-COPPER"))).willReturn(Cart.empty());

    // Act & Assert
    mvc.delete()
        .uri("/api/cart/items/{sku}", "SF-TITAN-COPPER")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.items")
        .asList()
        .isEmpty();
  }
}
