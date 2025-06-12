# --- Etapa 1: Construcción (Build Stage) ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos pom.xml y el submódulo para cachear dependencias
COPY pom.xml .
COPY shared-dtos-module ./shared-dtos-module
RUN mvn dependency:go-offline -B

# Copiamos el resto del código fuente y construimos
COPY src ./src
RUN mvn package -DskipTests

# --- Etapa 2: Ejecución (Runtime Stage) ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# ¡IMPORTANTE! Cambia el nombre del JAR al de este microservicio
COPY --from=build /app/target/dndms-ms2-character-roster-0.0.1-SNAPSHOT.jar app.jar

# Puerto que la aplicación expondrá dentro del contenedor
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]