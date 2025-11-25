# Stage 1: Build the JAR
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Встановлюємо робочий каталог
WORKDIR /app

# Копіюємо кореневий pom.xml і pom.xml модуля для кешування залежностей
COPY . .

# Завантажуємо залежності для всього проєкту (включаючи підмодуль)
RUN mvn -f pom.xml -pl auth-service dependency:go-offline -B

# Копіюємо весь код проєкту
COPY . .

# Збираємо JAR для auth-service
RUN mvn -f pom.xml -pl auth-service clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Копіюємо зібраний jar з попереднього етапу
COPY --from=build /app/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar ./auth-service.jar

# Виставляємо порт
EXPOSE 8081

# Команда запуску
ENTRYPOINT ["java", "-jar", "auth-service.jar"]