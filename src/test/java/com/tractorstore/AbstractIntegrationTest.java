package com.tractorstore;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base para tests que necesitan PostgreSQL real, con la conexión cableada automáticamente a Spring
 * vía {@code @ServiceConnection}: no hace falta declarar {@code spring.datasource.*} a mano.
 *
 * <p>El contenedor se arranca a mano en el bloque estático (patrón "singleton container" de
 * Testcontainers) en vez de usar {@code @Testcontainers}/{@code @Container}: ese mecanismo gestiona
 * el ciclo de vida por clase de test, y como {@code POSTGRES} es un campo estático heredado (una
 * sola instancia real para todas las subclases), la primera clase en terminar lo detenía en su
 * {@code afterAll} — las siguientes clases fallaban con "Connection refused" al puerto ya liberado.
 * Sin parar el contenedor a mano, el reaper de Testcontainers (Ryuk) lo limpia solo al terminar la
 * JVM.
 *
 * <p>{@code @ActiveProfiles("test")} activa {@code application-test.yml} en vez del perfil {@code
 * dev} por defecto.
 */
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

  @ServiceConnection
  static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.6-alpine");

  static {
    POSTGRES.start();
  }
}
