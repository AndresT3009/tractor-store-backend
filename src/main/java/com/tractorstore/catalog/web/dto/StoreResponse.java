package com.tractorstore.catalog.web.dto;

import com.tractorstore.catalog.domain.Store;

public record StoreResponse(
    String id, String name, String addressLine, String city, String imageUrl) {

  public static StoreResponse from(Store store) {
    return new StoreResponse(
        store.id(), store.name(), store.addressLine(), store.city(), store.imageUrl());
  }
}
