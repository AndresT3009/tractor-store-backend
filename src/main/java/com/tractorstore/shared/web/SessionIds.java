package com.tractorstore.shared.web;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resuelve el identificador de sesión anónima que identifica el carrito de un visitante.
 *
 * <p>Usa la sesión HTTP del contenedor (cookie {@code JSESSIONID}, HttpOnly por defecto en Spring
 * Boot): no hace falta infraestructura adicional para esta fase. Migrar a JWT con usuario
 * autenticado es explícitamente un paso posterior (Fase B8).
 */
public final class SessionIds {

  private SessionIds() {}

  public static String resolve(HttpServletRequest request) {
    return request.getSession(true).getId();
  }
}
