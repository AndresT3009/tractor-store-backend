/**
 * Módulo Catalog: productos, categorías, tiendas y recomendaciones.
 *
 * <p>Expone dos interfaces nombradas para el resto de módulos: {@code domain} (los value objects
 * {@code Product}, {@code Variant}, {@code Store}, {@code ProductCategory}) y {@code application}
 * (el servicio {@code CatalogService}). Todo lo demás (persistencia JPA, controlador REST) es
 * interno y no puede importarse desde otro módulo.
 */
package com.tractorstore.catalog;
