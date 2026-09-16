package com.tractorstore.inventory.web;

import static org.mockito.BDDMockito.given;

import com.tractorstore.inventory.application.InventoryService;
import com.tractorstore.inventory.domain.StockLevel;
import com.tractorstore.shared.web.SecurityConfig;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(InventoryController.class)
@Import(SecurityConfig.class)
class InventoryControllerTest {

  @Autowired private MockMvcTester mvc;

  @MockitoBean private InventoryService inventoryService;

  @Test
  void should_returnStock_when_skuExists() {
    // Arrange
    given(inventoryService.stockOf("SF-TITAN-COPPER"))
        .willReturn(Optional.of(new StockLevel("SF-TITAN-COPPER", 6)));

    // Act & Assert
    mvc.get()
        .uri("/api/inventory/{sku}", "SF-TITAN-COPPER")
        .assertThat()
        .hasStatusOk()
        .bodyJson()
        .extractingPath("$.available")
        .isEqualTo(true);
  }

  @Test
  void should_return404_when_skuIsUnknown() {
    // Arrange
    given(inventoryService.stockOf("unknown")).willReturn(Optional.empty());

    // Act & Assert
    mvc.get().uri("/api/inventory/{sku}", "unknown").assertThat().hasStatus(404);
  }
}
