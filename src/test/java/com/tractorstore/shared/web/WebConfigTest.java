package com.tractorstore.shared.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = WebConfigTest.PingController.class)
@Import({WebConfig.class, SecurityConfig.class, WebConfigTest.PingController.class})
@TestPropertySource(properties = "tractor-store.cors.allowed-origins=http://localhost:4200")
class WebConfigTest {

  @Autowired private MockMvcTester mvc;

  @Test
  void should_allowConfiguredOrigin_when_requestComesFromIt() {
    // Arrange, Act & Assert
    mvc.get()
        .uri("/api/ping")
        .header("Origin", "http://localhost:4200")
        .assertThat()
        .hasStatusOk()
        .hasHeader("Access-Control-Allow-Origin", "http://localhost:4200");
  }

  @Test
  void should_rejectUnconfiguredOrigin() {
    // Arrange, Act & Assert
    mvc.get()
        .uri("/api/ping")
        .header("Origin", "https://not-allowed.example.com")
        .assertThat()
        .doesNotContainHeader("Access-Control-Allow-Origin");
  }

  @RestController
  static class PingController {
    @GetMapping("/api/ping")
    String ping() {
      return "pong";
    }
  }
}
