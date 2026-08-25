package com.tractorstore.shared.web;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita CORS para la API a los orígenes configurados por ambiente.
 *
 * <p>Declara {@link CorsProperties} aquí mismo (en vez de depender solo de un
 * {@code @ConfigurationPropertiesScan} global) porque Spring Boot registra automáticamente
 * cualquier {@link WebMvcConfigurer} del classpath en TODOS los slices {@code @WebMvcTest}, no solo
 * en el que lo importa explícitamente. Sin esto, un slice test de otro controlador fallaría al
 * intentar construir este bean sin tener sus propiedades disponibles.
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
class WebConfig implements WebMvcConfigurer {

  private final CorsProperties corsProperties;

  WebConfig(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/api/**")
        .allowedOrigins(corsProperties.allowedOrigins().toArray(new String[0]))
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowCredentials(true);
  }
}
