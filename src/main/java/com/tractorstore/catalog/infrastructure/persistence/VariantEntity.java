package com.tractorstore.catalog.infrastructure.persistence;

import com.tractorstore.catalog.domain.Variant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Fila JPA de una variante de color. Nunca se expone fuera de infrastructure: ver {@link
 * #toDomain()}.
 */
@Entity
@Table(name = "catalog_variant")
class VariantEntity {

  @Id private String sku;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @Column(nullable = false)
  private int position;

  @Column(name = "color_name", nullable = false)
  private String colorName;

  @Column(name = "color_hex", nullable = false)
  private String colorHex;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  protected VariantEntity() {}

  VariantEntity(String sku, int position, String colorName, String colorHex, String imageUrl) {
    this.sku = sku;
    this.position = position;
    this.colorName = colorName;
    this.colorHex = colorHex;
    this.imageUrl = imageUrl;
  }

  void assignTo(ProductEntity product) {
    this.product = product;
  }

  Variant toDomain() {
    return new Variant(sku, colorName, colorHex, imageUrl);
  }
}
