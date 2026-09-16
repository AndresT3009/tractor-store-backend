package com.tractorstore.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Autentica la petición cuando ya existe una sesión HTTP, sin usuario ni contraseña.
 *
 * <p>Es la interpretación "carrito anónimo" de la Fase B8: un pedido solo puede confirmarse desde
 * una sesión que ya interactuó con el carrito (la creó {@link SessionIds#resolve}), no desde un
 * usuario autenticado. A propósito usa {@link HttpServletRequest#getSession(boolean)
 * getSession(false)}: no crea sesión como {@code SessionIds.resolve} sí hace, para que una petición
 * sin sesión previa llegue sin autenticar y {@code /api/orders/**} la rechace.
 */
@Component
class CartSessionAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null) {
      var authentication =
          new PreAuthenticatedAuthenticationToken(
              session.getId(), null, List.of(new SimpleGrantedAuthority("ROLE_CART_SESSION")));
      authentication.setAuthenticated(true);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }
}
