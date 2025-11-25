# 1. Stage: build the jar
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (caching layer)
COPY pom.xml .
COPY ../pom.xml ../pom.xml
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# 2. Stage: run the jar
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Expose service port
EXPOSE 8081

# Run the service
ENTRYPOINT ["java", "-jar", "/app/app.jar"]