# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the Spring Boot project folder
COPY poultry-shop ./poultry-shop

# Move into the actual Maven project directory
WORKDIR /app/poultry-shop

# Build the jar
RUN mvn clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar from correct target folder
COPY --from=build /app/poultry-shop/target/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
CMD ["java", "-jar", "app.jar"]


