package com.tractorstore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * Prueba de punta a punta (Spring context real, Postgres real) de la regla de seguridad de la Fase
 * B8: catálogo/inventario/carrito públicos, {@code /api/orders/**} exige una sesión ya existente
 * (no un usuario autenticado) — ver {@code shared.web.SecurityConfig} y {@code
 * CartSessionAuthenticationFilter}. También cubre Actuator y el {@code traceId} de la Fase B11 (ver
 * {@code shared.web.TraceIdFilter}): se añaden aquí en vez de en una clase nueva a propósito, para
 * no sumar otro contexto Spring con su propio contenedor Testcontainers — con dos clases separadas
 * pero con la misma configuración de test, Spring reutiliza el mismo contexto igualmente, pero la
 * carga extra de arrancar y tirar contenedores Postgres adicionales resultó ser suficiente para
 * disparar de forma reproducible una falla de red intermitente de Docker Desktop en Windows.
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

  // Usa COMMANDER-TEAL, no SF-TITAN-COPPER: este pedido real descuenta stock de verdad en la
  // base compartida por el resto de las clases de test (ver AbstractIntegrationTest), y
  // JpaInventoryRepositoryIntegrationTest verifica el valor exacto sembrado de SF-TITAN-COPPER —
  // comprarlo aquí dejaría ese otro test en rojo según qué clase corra primero.
  @Test
  void should_allowPlacingOrder_whenSessionWasEstablishedByAddingToCart() {
    // Arrange
    MockHttpSession session = new MockHttpSession();
    mvc.post()
        .uri("/api/cart/items")
        .session(session)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"sku\":\"COMMANDER-TEAL\"}")
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

  @Test
  void should_exposeHealth_asUp() {
    mvc.get()
        .uri("/actuator/health")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.status")
        .isEqualTo("UP");
  }

  @Test
  void should_exposePrometheusMetrics_taggedWithApplicationName() throws Exception {
    MvcTestResult result = mvc.get().uri("/actuator/prometheus").exchange();

    assertThat(result).hasStatusOk();
    assertThat(result.getResponse().getContentAsString())
        .contains("application=\"tractor-store-backend\"");
  }

  @Test
  void should_respectIncomingTraceId_endToEnd() {
    mvc.get()
        .uri("/api/catalog/home")
        .header("X-Trace-Id", "e2e-trace-42")
        .assertThat()
        .hasStatusOk()
        .hasHeader("X-Trace-Id", "e2e-trace-42");
  }

  @Test
  void should_generateTraceId_whenFrontendDidNotSendOne() {
    mvc.get().uri("/api/catalog/home").assertThat().hasStatusOk().containsHeader("X-Trace-Id");
  }
}
