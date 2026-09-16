package com.tractorstore;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Reglas de arquitectura que Spring Modulith no cubre por sí solo: la separación por capas
 * <em>dentro</em> de un mismo módulo (Modulith solo protege fronteras <em>entre</em> módulos).
 */
@AnalyzeClasses(
    packagesOf = TractorStoreBackendApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  @ArchTest
  static final ArchRule domainMustNotDependOnSpringOrJpa =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .and()
          .doNotHaveSimpleName("package-info")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework..", "jakarta.persistence..");

  @ArchTest
  static final ArchRule controllersMustNotAccessPersistenceDirectly =
      noClasses()
          .that()
          .resideInAPackage("..web..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..infrastructure.persistence..");

  @ArchTest
  static final ArchRule noFieldInjection = noFields().should().beAnnotatedWith(Autowired.class);
}
