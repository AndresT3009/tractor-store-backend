package com.tractorstore.catalog.web.dto;

import java.util.List;

public record CategoryResponse(
    List<ProductSummaryResponse> products, List<String> availableFilters) {}
