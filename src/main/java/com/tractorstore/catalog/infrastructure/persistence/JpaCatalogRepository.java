package com.tractorstore.catalog.infrastructure.persistence;

import com.tractorstore.catalog.application.CatalogRepository;
import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCatalog;
import com.tractorstore.catalog.domain.Store;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga el catálogo completo desde PostgreSQL y lo convierte a objetos de dominio inmutables.
 *
 * <p>La conversión ({@code toDomain()}) ocurre dentro de la transacción de solo lectura, mientras
 * las colecciones lazy de Hibernate todavía están accesibles. Con {@code open-in-view: false}, el
 * {@link ProductCatalog} devuelto ya no tiene proxies de Hibernate: es seguro usarlo fuera de la
 * transacción, en el controlador o en cualquier otro sitio.
 *
 * <p>Fase B12: {@code @Cacheable} aquí (no en {@code CatalogService}) cachea el snapshot completo
 * una sola vez para los cinco casos de uso del servicio (home, categorías, producto, stores,
 * recomendaciones), que de otro modo dispararían la misma consulta repetida por request — ver
 * {@code shared.cache.CacheConfig} para el TTL.
 */
@Component
class JpaCatalogRepository implements CatalogRepository {

  private final ProductJpaRepository products;
  private final StoreJpaRepository stores;

  JpaCatalogRepository(ProductJpaRepository products, StoreJpaRepository stores) {
    this.products = products;
    this.stores = stores;
  }

  @Override
  @Cacheable("catalog")
  @Transactional(readOnly = true)
  public ProductCatalog load() {
    List<Product> productList = products.findAll().stream().map(ProductEntity::toDomain).toList();
    List<Store> storeList = stores.findAll().stream().map(StoreEntity::toDomain).toList();
    return new ProductCatalog(productList, storeList);
  }
}
