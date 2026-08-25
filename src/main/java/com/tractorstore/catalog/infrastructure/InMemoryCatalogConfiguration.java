package com.tractorstore.catalog.infrastructure;

import com.tractorstore.catalog.domain.Product;
import com.tractorstore.catalog.domain.ProductCatalog;
import com.tractorstore.catalog.domain.ProductCategory;
import com.tractorstore.catalog.domain.Store;
import com.tractorstore.catalog.domain.Variant;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Construye el {@link ProductCatalog} a partir de un dataset fijo en memoria.
 *
 * <p>Es un punto de reemplazo deliberado: cuando el catálogo migre a PostgreSQL (Fase B4), esta
 * clase desaparece y un repositorio JPA construye el mismo tipo de objeto de dominio. Ni el
 * servicio de aplicación ni el controlador REST necesitan cambiar.
 */
@Configuration
class InMemoryCatalogConfiguration {

  @Bean
  ProductCatalog productCatalog() {
    return new ProductCatalog(seedProducts(), seedStores());
  }

  private List<Product> seedProducts() {
    return List.of(
        new Product(
            "smartfarm-titan",
            "SmartFarm Titan",
            "Autonomous navigation across rough terrain",
            ProductCategory.AUTONOMOUS,
            new BigDecimal("4000.00"),
            List.of(
                "Autonomous navigation across rough terrain",
                "Solar-assisted drivetrain for all-day work",
                "Modular tool bay for field-specific attachments"),
            List.of(
                new Variant(
                    "SF-TITAN-COPPER",
                    "Sunset Copper",
                    "#C24914",
                    "/images/smartfarm-titan-copper.png"),
                new Variant(
                    "SF-TITAN-SAPPHIRE",
                    "Cosmic Sapphire",
                    "#1B3F91",
                    "/images/smartfarm-titan-sapphire.png"))),
        new Product(
            "heritage-workhorse",
            "Heritage Workhorse",
            "A dependable classic built for daily field work",
            ProductCategory.CLASSIC,
            new BigDecimal("5700.00"),
            List.of("Rugged cast-iron frame", "Simple mechanical controls"),
            List.of(
                new Variant(
                    "HERITAGE-GREEN",
                    "Heritage Green",
                    "#4C7A2E",
                    "/images/heritage-workhorse-green.png"))),
        new Product(
            "rapid-racer",
            "Rapid Racer",
            "Fast and nimble for smaller plots",
            ProductCategory.CLASSIC,
            new BigDecimal("7500.00"),
            List.of("Lightweight frame", "Tight turning radius"),
            List.of(
                new Variant(
                    "RAPID-BLUE", "Racing Blue", "#1D4FA3", "/images/rapid-racer-blue.png"))),
        new Product(
            "fieldmaster-classic",
            "Fieldmaster Classic",
            "The all-rounder for mixed terrain",
            ProductCategory.CLASSIC,
            new BigDecimal("6200.00"),
            List.of("Adjustable wheelbase"),
            List.of(
                new Variant(
                    "FIELDMASTER-PINK",
                    "Blossom Pink",
                    "#D46A9C",
                    "/images/fieldmaster-classic-pink.png"))),
        new Product(
            "countryside-commander",
            "Countryside Commander",
            "Autonomous power for large estates",
            ProductCategory.AUTONOMOUS,
            new BigDecimal("8900.00"),
            List.of("Full-day autonomous routes", "Obstacle detection"),
            List.of(
                new Variant(
                    "COMMANDER-TEAL",
                    "Countryside Teal",
                    "#1F7A6C",
                    "/images/countryside-commander-teal.png"))));
  }

  private List<Store> seedStores() {
    return List.of(
        new Store("aurora-flagship", "Aurora Flagship Store", "Astronaut Way 1", "Arlington"),
        new Store("big-micro-machines", "Big Micro Machines", "Broadway 2", "Burlington"),
        new Store("central-mall", "Central Mall", "Clown Street 3", "Cryo"),
        new Store("downtown-model", "Downtown Model Store", "Duck Street 4", "Davenport"));
  }
}
