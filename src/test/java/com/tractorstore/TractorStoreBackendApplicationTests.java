package com.tractorstore;

import static org.assertj.core.api.Assertions.assertThat;

import com.tractorstore.catalog.application.CatalogRepository;
import com.tractorstore.catalog.domain.ProductCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TractorStoreBackendApplicationTests extends AbstractIntegrationTest {

  @Autowired private CatalogRepository catalogRepository;

  @Test
  void contextLoads() {}

  /**
   * Fase B12: {@code JpaCatalogRepository#load()} está cacheado (ver {@code
   * shared.cache.CacheConfig}). Caffeine devuelve la misma instancia mientras el TTL no expire, así
   * que la igualdad de referencia entre dos llamadas seguidas es prueba directa de que la segunda
   * no volvió a golpear PostgreSQL.
   */
  @Test
  void should_cacheCatalogSnapshot_acrossRepeatedLoads() {
    ProductCatalog first = catalogRepository.load();
    ProductCatalog second = catalogRepository.load();

    assertThat(second).isSameAs(first);
  }
}
