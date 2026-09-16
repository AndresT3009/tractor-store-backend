package com.tractorstore.catalog.infrastructure.persistence;

import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCategory;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.BatchSize;

/**
 * Fila JPA de un producto. Nunca se expone fuera de infrastructure: ver {@link #toDomain()}.
 *
 * <p>{@code variants} y {@code highlights} usan {@link BatchSize} en vez de un {@code JOIN FETCH}
 * conjunto: cargar dos colecciones {@code List} distintas en la misma consulta produciría un
 * producto cartesiano (o {@code MultipleBagFetchException} en Hibernate). {@code BatchSize} agrupa
 * las consultas de colecciones de varios productos en lote, evitando el N+1 sin ese problema.
 */
@Entity
@Table(name = "catalog_product")
class ProductEntity {

  @Id private String id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProductCategory category;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @ElementCollection
  @CollectionTable(
      name = "catalog_product_highlight",
      joinColumns = @JoinColumn(name = "product_id"))
  @OrderColumn(name = "position")
  @Column(name = "highlight", nullable = false)
  @BatchSize(size = 20)
  private List<String> highlights = new ArrayList<>();

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
  @OrderBy("position ASC")
  @BatchSize(size = 20)
  private List<VariantEntity> variants = new ArrayList<>();

  protected ProductEntity() {}

  ProductEntity(
      String id, String name, String description, ProductCategory category, BigDecimal price) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.category = category;
    this.price = price;
  }

  void addHighlight(String highlight) {
    highlights.add(highlight);
  }

  void addVariant(VariantEntity variant) {
    variant.assignTo(this);
    variants.add(variant);
  }

  Product toDomain() {
    return new Product(
        id,
        name,
        description,
        category,
        price,
        List.copyOf(highlights),
        variants.stream().map(VariantEntity::toDomain).toList());
  }
}
