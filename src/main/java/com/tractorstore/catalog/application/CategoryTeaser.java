package com.tractorstore.catalog.application;

import com.tractorstore.catalog.domain.ProductCategory;

/** Teaser destacado de una categoría para la home, con imagen y título curados. */
public record CategoryTeaser(ProductCategory category, String title, String imageUrl) {}
