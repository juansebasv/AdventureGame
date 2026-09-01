# ============================================================================
# Build en dos etapas. Todo ocurre dentro de Docker: no hace falta instalar
# Java ni Gradle en la máquina anfitriona.
#
#   Gradle 6.9 / Spring Boot 2.4 / springfox 2.9.2 NO funcionan con JDK 17,
#   por eso el build y el runtime usan JDK 11.
# ============================================================================

# ---- Etapa 1: compilar el JAR ejecutable ---------------------------------
FROM gradle:6.9.4-jdk11 AS build
WORKDIR /home/gradle/project

# Metadatos de build primero -> se cachea la resolución de dependencias
COPY settings.gradle build.gradle lombok.config ./
COPY gradle ./gradle
RUN gradle --no-daemon dependencies --refresh-dependencies > /dev/null 2>&1 || true

# Código fuente y empaquetado (bootJar no ejecuta tests -> build autónomo)
COPY src ./src
RUN gradle --no-daemon clean bootJar

# ---- Etapa 2: imagen de runtime ----------------------------------------
FROM eclipse-temurin:11-jre

# curl: sólo para el HEALTHCHECK contra el endpoint de Actuator
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --home /app app

COPY --from=build --chown=app:app /home/gradle/project/build/libs/*.jar /app/app.jar
USER app

EXPOSE 9697

HEALTHCHECK --interval=15s --timeout=5s --start-period=45s --retries=5 \
    CMD curl -fsS http://localhost:9697/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
