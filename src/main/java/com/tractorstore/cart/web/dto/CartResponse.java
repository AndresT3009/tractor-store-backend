package com.tractorstore.cart.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    List<CartLineResponse> items, int totalQuantity, BigDecimal totalPrice) {}
