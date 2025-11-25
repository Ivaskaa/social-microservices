# Встановлюємо Maven та JDK
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Копіюємо весь код багатомодульного проєкту
COPY . .

# Завантажуємо залежності
RUN mvn dependency:go-offline

# Будуємо jar
RUN mvn clean package -DskipTests

# Фінальний образ
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar ./auth-service.jar

EXPOSE 8081

CMD ["java", "-jar", "auth-service.jar"]