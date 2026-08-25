package com.tractorstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Test de arquitectura: verifica que los módulos (catalog, inventory, cart, order, shared) respetan
 * sus fronteras — ningún módulo puede importar un tipo interno de otro. Si esto falla, el mensaje
 * de Spring Modulith señala exactamente qué clase rompió qué regla.
 */
class ModularityTests {

  private static final ApplicationModules MODULES =
      ApplicationModules.of(TractorStoreBackendApplication.class);

  @Test
  void modulesRespectTheirBoundaries() {
    MODULES.verify();
  }

  @Test
  void writeDocumentationSnippets() {
    new Documenter(MODULES).writeDocumentation();
  }
}
