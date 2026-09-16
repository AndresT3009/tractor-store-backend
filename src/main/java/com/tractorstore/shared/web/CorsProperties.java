package com.tractorstore.shared.web;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Orígenes permitidos por CORS, configurables por ambiente vía {@code CORS_ALLOWED_ORIGINS}. */
@ConfigurationProperties(prefix = "tractor-store.cors")
public record CorsProperties(List<String> allowedOrigins) {}
