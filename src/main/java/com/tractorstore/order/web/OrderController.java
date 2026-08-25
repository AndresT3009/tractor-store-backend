package com.tractorstore.order.web;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.order.application.OrderService;
import com.tractorstore.order.domain.Order;
import com.tractorstore.order.web.dto.OrderResponse;
import com.tractorstore.order.web.dto.PlaceOrderRequest;
import com.tractorstore.shared.web.SessionIds;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Endpoints del módulo Order, consumidos por mfe-checkout.
 *
 * <p>Orquesta explícitamente el flujo de checkout (leer carrito de la sesión → validar → crear
 * pedido → vaciar carrito) porque Cart y Order todavía se comunican por llamada directa, no por
 * eventos: ese desacople es la Fase B10, pendiente.
 */
@RestController
@RequestMapping("/api/orders")
class OrderController {

  private final OrderService orderService;
  private final CartService cartService;
  private final CatalogService catalogService;

  OrderController(
      OrderService orderService, CartService cartService, CatalogService catalogService) {
    this.orderService = orderService;
    this.cartService = cartService;
    this.catalogService = catalogService;
  }

  @PostMapping
  ResponseEntity<OrderResponse> placeOrder(
      @RequestBody PlaceOrderRequest request, HttpServletRequest httpRequest) {
    String sessionId = SessionIds.resolve(httpRequest);
    Cart cart = cartService.getCart(sessionId);
    if (cart.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
    }
    boolean storeExists =
        catalogService.stores().stream().anyMatch(store -> store.id().equals(request.storeId()));
    if (!storeExists) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Tienda desconocida: " + request.storeId());
    }

    Order order =
        orderService.placeOrder(request.firstName(), request.lastName(), request.storeId(), cart);
    cartService.clear(sessionId);

    return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
  }

  @GetMapping("/{id}")
  OrderResponse getOrder(@PathVariable String id) {
    return orderService
        .findById(id)
        .map(OrderResponse::from)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
  }
}
