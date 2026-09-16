package com.tractorstore.shared.time;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expone el reloj del sistema como bean inyectable, para que el código de negocio no llame a {@code
 * Instant.now()} directamente y sea determinista en tests.
 */
@Configuration
class ClockConfig {

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }
}
