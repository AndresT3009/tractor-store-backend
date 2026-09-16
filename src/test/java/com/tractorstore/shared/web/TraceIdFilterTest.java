package com.tractorstore.shared.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class TraceIdFilterTest {

  private final TraceIdFilter filter = new TraceIdFilter();

  @Test
  void should_generateTraceId_whenRequestArrivesWithoutOne() throws Exception {
    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicReference<String> traceIdDuringChain = new AtomicReference<>();

    // Act
    filter.doFilter(request, response, (req, res) -> traceIdDuringChain.set(MDC.get("traceId")));

    // Assert
    assertThat(traceIdDuringChain.get()).isNotBlank();
    assertThat(response.getHeader("X-Trace-Id")).isEqualTo(traceIdDuringChain.get());
    assertThat(MDC.get("traceId")).isNull();
  }

  @Test
  void should_respectIncomingTraceId_whenFrontendAlreadySentOne() throws Exception {
    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Trace-Id", "frontend-trace-123");
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicReference<String> traceIdDuringChain = new AtomicReference<>();

    // Act
    filter.doFilter(request, response, (req, res) -> traceIdDuringChain.set(MDC.get("traceId")));

    // Assert
    assertThat(traceIdDuringChain.get()).isEqualTo("frontend-trace-123");
    assertThat(response.getHeader("X-Trace-Id")).isEqualTo("frontend-trace-123");
  }

  @Test
  void should_clearMdc_evenWhenChainThrows() {
    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    // Act & Assert
    org.junit.jupiter.api.Assertions.assertThrows(
        java.io.IOException.class,
        () ->
            filter.doFilter(
                request,
                response,
                (req, res) -> {
                  throw new java.io.IOException("boom");
                }));
    assertThat(MDC.get("traceId")).isNull();
  }
}
