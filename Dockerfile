# syntax=docker/dockerfile:1

# Build en dos etapas: la imagen final solo lleva el JRE y el jar, no el JDK ni el repositorio
# Maven completo. mvnw dependency:go-offline en su propia capa aprovecha la cache de Docker: si
# solo cambia código fuente (no el pom.xml), esta capa no se reconstruye.
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S tractorstore && adduser -S tractorstore -G tractorstore
COPY --from=build /app/target/*.jar app.jar
USER tractorstore

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
