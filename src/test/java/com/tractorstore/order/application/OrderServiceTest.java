package com.tractorstore.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.domain.Variant;
import com.tractorstore.inventory.application.InventoryService;
import com.tractorstore.inventory.domain.InsufficientStockException;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.domain.OrderLine;
import com.tractorstore.shared.events.OrderPlaced;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  private static final Instant FIXED_INSTANT = Instant.parse("2026-08-25T10:00:00Z");
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

  @Mock private OrderRepository orderRepository;
  @Mock private CatalogService catalogService;
  @Mock private InventoryService inventoryService;
  @Mock private ApplicationEventPublisher eventPublisher;

  private OrderService orderService;

  @BeforeEach
  void setUp() {
    Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    orderService =
        new OrderService(
            orderRepository, catalogService, inventoryService, eventPublisher, fixedClock);
  }

  @Test
  void should_buildOrderWithSnapshotPrices_when_placingIt() {
    // Arrange
    Cart cart = new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 2)));
    given(catalogService.findProductByVariantSku("SF-TITAN-COPPER"))
        .willReturn(Optional.of(SMARTFARM_TITAN));

    // Act
    Order order = orderService.placeOrder("Ada", "Lovelace", "aurora-flagship", cart, "session-1");

    // Assert
    assertThat(order.firstName()).isEqualTo("Ada");
    assertThat(order.storeId()).isEqualTo("aurora-flagship");
    assertThat(order.placedAt()).isEqualTo(FIXED_INSTANT);
    assertThat(order.lines()).hasSize(1);
    assertThat(order.lines().get(0).unitPrice()).isEqualByComparingTo("4000.00");
    assertThat(order.totalPrice()).isEqualByComparingTo("8000.00");
    verify(orderRepository).save(order);
    verify(eventPublisher).publishEvent(new OrderPlaced(order.id(), "session-1"));
    verify(inventoryService).decrementStock("SF-TITAN-COPPER", 2);
  }

  @Test
  void should_rejectOrderAndNotSaveIt_when_stockIsNotEnough() {
    // Arrange
    Cart cart = new Cart(List.of(new CartLineItem("SF-TITAN-COPPER", 2)));
    given(catalogService.findProductByVariantSku("SF-TITAN-COPPER"))
        .willReturn(Optional.of(SMARTFARM_TITAN));
    willThrow(new InsufficientStockException("SF-TITAN-COPPER"))
        .given(inventoryService)
        .decrementStock("SF-TITAN-COPPER", 2);

    // Act & Assert
    assertThatThrownBy(
            () -> orderService.placeOrder("Ada", "Lovelace", "aurora-flagship", cart, "session-1"))
        .isInstanceOf(InsufficientStockException.class);
    verify(orderRepository, never()).save(any());
  }

  @Test
  void should_rejectOrder_when_cartSkuNoLongerExistsInCatalog() {
    // Arrange
    Cart cart = new Cart(List.of(new CartLineItem("GHOST-SKU", 1)));
    given(catalogService.findProductByVariantSku("GHOST-SKU")).willReturn(Optional.empty());

    // Act & Assert
    assertThatIllegalStateException()
        .isThrownBy(
            () -> orderService.placeOrder("Ada", "Lovelace", "aurora-flagship", cart, "session-1"));
  }

  @Test
  void should_delegateToRepository_when_findingById() {
    // Arrange
    Order order =
        new Order(
            "order-1",
            "Ada",
            "Lovelace",
            "aurora-flagship",
            List.of(new OrderLine("SF-TITAN-COPPER", 1, new BigDecimal("4000.00"))),
            FIXED_INSTANT);
    given(orderRepository.findById("order-1")).willReturn(Optional.of(order));

    // Act
    var result = orderService.findById("order-1");

    // Assert
    assertThat(result).contains(order);
  }
}
