package com.tractorstore.catalog.application;

import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.Variant;

/** Una variante recomendada junto con el producto al que pertenece, para poder mostrar precio. */
public record RecommendedVariant(Variant variant, Product product) {}
