package com.tractorstore.cart.web.dto;

import java.math.BigDecimal;

public record CartLineResponse(
    String sku,
    String productId,
    String productName,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal subtotal,
    String imageUrl) {}
