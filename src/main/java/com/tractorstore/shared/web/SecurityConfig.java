package com.tractorstore.shared.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Fase B8: catálogo, inventario y carrito quedan públicos; confirmar o consultar un pedido exige
 * una sesión de carrito ya existente (ver {@link CartSessionAuthenticationFilter}), no un usuario
 * autenticado — la guía deja explícitamente esa alternativa ("mantienes la cookie de sesión para el
 * carrito anónimo") en vez de Keycloak/JWT, decisión tomada con el usuario.
 *
 * <p>CSRF desactivado a propósito: es una API JSON consumida por una SPA vía CORS con orígenes
 * explícitos ({@link CorsProperties}), no un flujo de formularios/login tradicional donde CSRF
 * aplicaría. Las peticiones {@code OPTIONS} (preflight de CORS) se permiten siempre: el navegador
 * las envía sin credenciales, así que exigir sesión ahí rompería el CORS de {@code /api/orders/**}
 * para el propio frontend legítimo.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, CartSessionAuthenticationFilter cartSessionAuthenticationFilter)
      throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers("/api/orders/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            exceptions ->
                exceptions.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .addFilterBefore(cartSessionAuthenticationFilter, AnonymousAuthenticationFilter.class);
    return http.build();
  }
}
