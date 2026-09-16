package com.tractorstore.order.web.dto;

import com.tractorstore.order.domain.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    String id,
    String firstName,
    String lastName,
    String storeId,
    List<OrderLineResponse> lines,
    BigDecimal totalPrice,
    Instant placedAt) {

  public static OrderResponse from(Order order) {
    return new OrderResponse(
        order.id(),
        order.firstName(),
        order.lastName(),
        order.storeId(),
        order.lines().stream().map(OrderLineResponse::from).toList(),
        order.totalPrice(),
        order.placedAt());
  }
}
