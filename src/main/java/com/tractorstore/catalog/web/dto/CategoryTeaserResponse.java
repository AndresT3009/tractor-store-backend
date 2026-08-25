package com.tractorstore.catalog.web.dto;

import com.tractorstore.catalog.application.CategoryTeaser;
import java.util.Locale;

public record CategoryTeaserResponse(String category, String title, String imageUrl) {

  public static CategoryTeaserResponse from(CategoryTeaser teaser) {
    return new CategoryTeaserResponse(
        teaser.category().name().toLowerCase(Locale.ROOT), teaser.title(), teaser.imageUrl());
  }
}
