package com.tractorstore.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Fase B11: correlaciona todos los logs de una misma request bajo un {@code traceId}. Si el
 * frontend ya envía {@code X-Trace-Id}, se respeta (permite trazar una request de punta a punta
 * frontend→backend); si no, se genera uno nuevo. El header se devuelve siempre en la respuesta.
 *
 * <p>{@code @Order(HIGHEST_PRECEDENCE)} para que el traceId esté en el MDC antes que cualquier otro
 * filtro (incluida la cadena de Spring Security) llegue a loguear algo de esta request. Al ser un
 * bean {@link jakarta.servlet.Filter}, Spring Boot lo registra automáticamente para todas las
 * rutas, sin cablearlo a mano (a diferencia de {@link CartSessionAuthenticationFilter}, que sí se
 * añade explícitamente a la cadena de seguridad porque participa en autenticación).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

  static final String TRACE_ID_HEADER = "X-Trace-Id";
  static final String MDC_KEY = "traceId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String traceId = request.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString();
    }
    response.setHeader(TRACE_ID_HEADER, traceId);
    MDC.put(MDC_KEY, traceId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
