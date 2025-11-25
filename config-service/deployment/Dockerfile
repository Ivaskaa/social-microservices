# Stage 1: Build the JAR
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# 1. Copy the root pom and the pom of each microservice
COPY pom.xml ./pom.xml
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY config-service/pom.xml ./config-service/pom.xml
COPY discovery-service/pom.xml ./discovery-service/pom.xml
COPY gateway-service/pom.xml ./gateway-service/pom.xml
COPY user-service/pom.xml ./user-service/pom.xml
COPY post-service/pom.xml ./post-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml

# 2. Download all dependencies once
RUN mvn dependency:go-offline -B

# 3. Copy all the code after caching dependencies
COPY . .

# 4. Compile JARs for a specific microservice
RUN mvn -f pom.xml -pl config-service clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/config-service/target/config-service-0.0.1-SNAPSHOT.jar ./config-service.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "config-service.jar"]