# Estrategia de testing

## Niveles

- **Unitarios** (`domain`, `application`): JUnit 5 + AssertJ, sin Spring context. Verifican reglas de
  negocio puras (cálculo de totales, transiciones de estado) sin pagar el costo de levantar el
  framework.
- **Integración con Testcontainers**: `@SpringBootTest` + PostgreSQL real en contenedor (nunca H2 u
  otra base en memoria) para los repositorios JPA y los flujos web (`@AutoConfigureMockMvc`). Requiere
  Docker corriendo; en CI ya viene preinstalado en los runners de GitHub.
- **Arquitectura**: `ArchitectureTest` (ArchUnit) fija las reglas de capas (`domain` no depende de
  Spring/JPA, `infrastructure.persistence` no es visible fuera de su módulo) como test, no como
  convención de código review. `ModularityTests` (`ApplicationModules.verify()` de Spring Modulith)
  verifica que los módulos no se salten sus fronteras declaradas.

Los cuatro tipos corren juntos con `./mvnw verify` — no hay un comando separado por nivel, porque
todos son JUnit normal en `src/test` y Surefire/Failsafe ya los agrupa.

## Cobertura

JaCoCo (`org.jacoco:jacoco-maven-plugin`) instrumenta cada `mvn test` y deja el reporte en
`target/site/jacoco/index.html`. Cobertura actual de línea: ~90%.

Umbrales que se van a aplicar en SonarCloud una vez esté conectado el repo:

| Métrica | Umbral |
| --- | --- |
| Cobertura global | 80% |
| Cobertura de código nuevo (New Code) | 85% |
| Duplicación de código nuevo | < 3% |

El foco es **código nuevo**, no perseguir el 100% en código legado: es el umbral que SonarCloud aplica
por defecto en su Quality Gate ("Sonar way") y el que tiene sentido para un repo que ya arrancó con
buena cobertura de base.

## Qué no se testea (y por qué)

- Getters/setters, DTOs sin lógica, configuración declarativa (`@Configuration` que solo registra
  beans): no tienen ramas que cubrir, y contarlos infla el número sin aportar señal.
- El arranque de `main()` (`TractorStoreBackendApplicationTests`) solo confirma que el contexto de
  Spring carga — es una red de seguridad barata, no un lugar para lógica de negocio.
