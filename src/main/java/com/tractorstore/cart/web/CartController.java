package com.tractorstore.cart.web;

import com.tractorstore.cart.application.CartService;
import com.tractorstore.cart.domain.Cart;
import com.tractorstore.cart.domain.CartLineItem;
import com.tractorstore.cart.web.dto.AddCartItemRequest;
import com.tractorstore.cart.web.dto.CartLineResponse;
import com.tractorstore.cart.web.dto.CartResponse;
import com.tractorstore.cart.web.dto.MiniCartResponse;
import com.tractorstore.catalog.application.CatalogService;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.Variant;
import com.tractorstore.shared.web.SessionIds;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoints del módulo Cart, consumidos por mfe-checkout. */
@RestController
@RequestMapping("/api/cart")
class CartController {

  private final CartService cartService;
  private final CatalogService catalogService;

  CartController(CartService cartService, CatalogService catalogService) {
    this.cartService = cartService;
    this.catalogService = catalogService;
  }

  @GetMapping
  CartResponse cart(HttpServletRequest request) {
    return toResponse(cartService.getCart(SessionIds.resolve(request)));
  }

  @GetMapping("/mini")
  MiniCartResponse mini(HttpServletRequest request) {
    return new MiniCartResponse(cartService.getCart(SessionIds.resolve(request)).totalQuantity());
  }

  @PostMapping("/items")
  CartResponse addItem(@RequestBody AddCartItemRequest body, HttpServletRequest request) {
    return toResponse(cartService.addItem(SessionIds.resolve(request), body.sku()));
  }

  @DeleteMapping("/items/{sku}")
  CartResponse removeItem(@PathVariable String sku, HttpServletRequest request) {
    return toResponse(cartService.removeItem(SessionIds.resolve(request), sku));
  }

  private CartResponse toResponse(Cart cart) {
    var lines = cart.items().stream().map(this::toLineResponse).toList();
    BigDecimal totalPrice =
        lines.stream().map(CartLineResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    return new CartResponse(lines, cart.totalQuantity(), totalPrice);
  }

  private CartLineResponse toLineResponse(CartLineItem item) {
    Product product =
        catalogService
            .findProductByVariantSku(item.sku())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El SKU en el carrito ya no existe en el catálogo: " + item.sku()));
    Variant variant = product.variantBySku(item.sku()).orElseThrow();
    BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(item.quantity()));
    return new CartLineResponse(
        item.sku(),
        product.id(),
        product.name(),
        product.price(),
        item.quantity(),
        subtotal,
        variant.imageUrl());
  }
}
