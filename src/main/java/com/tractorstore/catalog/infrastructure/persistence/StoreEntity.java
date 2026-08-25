package com.tractorstore.catalog.infrastructure.persistence;

import com.tractorstore.catalog.domain.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Fila JPA de una tienda. Nunca se expone fuera de infrastructure: ver {@link #toDomain()}. */
@Entity
@Table(name = "catalog_store")
class StoreEntity {

  @Id private String id;

  @Column(nullable = false)
  private String name;

  @Column(name = "address_line", nullable = false)
  private String addressLine;

  @Column(nullable = false)
  private String city;

  protected StoreEntity() {}

  StoreEntity(String id, String name, String addressLine, String city) {
    this.id = id;
    this.name = name;
    this.addressLine = addressLine;
    this.city = city;
  }

  Store toDomain() {
    return new Store(id, name, addressLine, city);
  }
}
