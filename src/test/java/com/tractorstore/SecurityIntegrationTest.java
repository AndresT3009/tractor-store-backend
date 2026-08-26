package com.tractorstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * Prueba de punta a punta (Spring context real, Postgres real) de la regla de seguridad de la Fase
 * B8: catálogo/inventario/carrito públicos, {@code /api/orders/**} exige una sesión ya existente
 * (no un usuario autenticado) — ver {@code shared.web.SecurityConfig} y {@code
 * CartSessionAuthenticationFilter}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvcTester mvc;

  @Test
  void should_allowCatalog_withoutAnySession() {
    mvc.get().uri("/api/catalog/home").assertThat().hasStatusOk();
  }

  @Test
  void should_rejectPlacingOrder_withoutAnExistingSession() {
    mvc.post()
        .uri("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"storeId\":\"aurora-flagship\"}")
        .assertThat()
        .hasStatus(401);
  }

  @Test
  void should_allowPlacingOrder_whenSessionWasEstablishedByAddingToCart() {
    // Arrange
    MockHttpSession session = new MockHttpSession();
    mvc.post()
        .uri("/api/cart/items")
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"sku\":\"SF-TITAN-COPPER\"}")
        .assertThat()
        .hasStatusOk();

    // Act & Assert
    mvc.post()
        .uri("/api/orders")
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content(
            "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"storeId\":\"aurora-flagship\"}")
        .assertThat()
        .hasStatus(201);
  }
}
