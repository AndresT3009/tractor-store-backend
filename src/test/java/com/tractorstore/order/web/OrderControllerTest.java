package com.tractorstore.order.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.Store;
import com.tractorstore.order.application.OrderService;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.domain.OrderLine;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

  private static final Store AURORA =
      new Store("aurora-flagship", "Aurora Flagship", "Astronaut Way 1", "Arlington");
  private static final Order PLACED_ORDER =
      new Order(
          "order-1",
          "Ada",
          "Lovelace",
          "aurora-flagship",
          List.of(new OrderLine("SF-TITAN-COPPER", 1, new BigDecimal("4000.00"))),
          Instant.parse("2026-08-25T10:00:00Z"));

  @Autowired private MockMvcTester mvc;

  @MockitoBean private OrderService orderService;
  @MockitoBean private CartService cartService;
  @MockitoBean private CatalogService catalogService;

  @Test
  void should_placeOrderAndClearCart_when_cartHasItemsAndStoreIsValid() {
    // Arrange
    given(cartService.getCart(anyString()))
        .willReturn(new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 1))));
    given(catalogService.stores()).willReturn(List.of(AURORA));
    given(
            orderService.placeOrder(
                "Ada",
                "Lovelace",
                "aurora-flagship",
                new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 1)))))
        .willReturn(PLACED_ORDER);

    // Act & Assert
    mvc.post()
        .uri("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"storeId\":\"aurora-flagship\"}")
        .assertThat()
        .hasStatus(201)
        .bodyJson()
        .extractingPath("$.id")
        .isEqualTo("order-1");
    verify(cartService).clear(anyString());
  }

  @Test
  void should_reject_when_cartIsEmpty() {
    // Arrange
    given(cartService.getCart(anyString())).willReturn(Cart.empty());

    // Act & Assert
    mvc.post()
        .uri("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"storeId\":\"aurora-flagship\"}")
        .assertThat()
        .hasStatus(400);
  }

  @Test
  void should_reject_when_storeIsUnknown() {
    // Arrange
    given(cartService.getCart(anyString()))
        .willReturn(new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 1))));
    given(catalogService.stores()).willReturn(List.of(AURORA));

    // Act & Assert
    mvc.post()
        .uri("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"storeId\":\"does-not-exist\"}")
        .assertThat()
        .hasStatus(400);
  }

  @Test
  void should_returnOrder_when_idExists() {
    // Arrange
    given(orderService.findById("order-1")).willReturn(Optional.of(PLACED_ORDER));

    // Act & Assert
    mvc.get()
        .uri("/api/orders/{id}", "order-1")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.firstName")
        .isEqualTo("Ada");
  }

  @Test
  void should_return404_when_orderIdIsUnknown() {
    // Arrange
    given(orderService.findById("unknown")).willReturn(Optional.empty());

    // Act & Assert
    mvc.get().uri("/api/orders/{id}", "unknown").assertThat().hasStatus(404);
  }
}
