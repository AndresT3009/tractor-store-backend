package com.tractorstore.catalog.application;

import com.tractorstore.catalog.domain.ProductCatalog;

/**
 * Puerto que la capa de aplicación usa para obtener el catálogo, sin saber si viene de memoria, de
 * PostgreSQL o de cualquier otra fuente. La implementación real vive en infrastructure.
 */
public interface CatalogRepository {

  ProductCatalog load();
}
