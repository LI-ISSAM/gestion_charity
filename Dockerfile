# ── Build stage ────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ── Run stage ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie le JAR
COPY --from=build /app/target/*.jar app.jar

# Crée le dossier uploads
RUN mkdir -p /app/uploads/organisations

# Port
EXPOSE 62851

# Lance l'application
ENTRYPOINT ["java", "-jar", "app.jar"]