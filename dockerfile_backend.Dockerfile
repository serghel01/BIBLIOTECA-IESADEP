# ---- Etapa de compilación ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom.xml y descargar dependencias primero (cache)
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY backend/src ./src

# Empaquetar la app
RUN mvn clean package -DskipTests

# ---- Etapa de ejecución ----
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiar el jar generado
COPY --from=build /app/target/*.jar app.jar

# Puerto de Spring Boot
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
