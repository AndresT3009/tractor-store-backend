package com.tractorstore.order.web.dto;

import com.tractorstore.order.domain.OrderLine;
import java.math.BigDecimal;

public record OrderLineResponse(
    String sku, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {

  public static OrderLineResponse from(OrderLine line) {
    return new OrderLineResponse(line.sku(), line.quantity(), line.unitPrice(), line.subtotal());
  }
}
