package com.tractorstore.shared.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fase B12: el catálogo cambia poco (es prácticamente estático fuera de las migraciones Flyway),
 * así que se cachea en memoria con un TTL fijo de 1 hora en vez de golpear PostgreSQL en cada
 * request — ver {@code catalog.infrastructure.persistence.JpaCatalogRepository#load()}.
 */
@Configuration
@EnableCaching
class CacheConfig {

  private static final String CATALOG_CACHE = "catalog";

  @Bean
  CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(CATALOG_CACHE);
    cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(1)));
    return cacheManager;
  }
}
