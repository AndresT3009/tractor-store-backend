package com.tractorstore;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base para tests que necesitan PostgreSQL real. Un único contenedor por clase de test
 * (Testcontainers lo reutiliza entre métodos), con la conexión cableada automáticamente a Spring
 * vía {@code @ServiceConnection}: no hace falta declarar {@code spring.datasource.*} a mano.
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

  @Container @ServiceConnection
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.6-alpine");
}
