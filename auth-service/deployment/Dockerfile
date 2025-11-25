# Stage 1: Build the JAR
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# 1. Копіюємо кореневий pom та pom кожного мікросервісу
COPY pom.xml ./pom.xml
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY config-service/pom.xml ./config-service/pom.xml
COPY discovery-service/pom.xml ./discovery-service/pom.xml
COPY gateway-service/pom.xml ./gateway-service/pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
COPY post-service/pom.xml ./post-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml

# 2. Завантажуємо всі залежності один раз
RUN mvn dependency:go-offline -B

# 3. Копіюємо весь код після кешування залежностей
COPY . .

# 4. Збираємо JAR для конкретного мікросервісу (наприклад auth-service)
RUN mvn -f pom.xml -pl auth-service clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar ./auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "auth-service.jar"]