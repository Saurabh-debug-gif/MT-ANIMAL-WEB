# Use Java 17 (good for Spring Boot)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy everything into container
COPY . .

# Build the app
RUN ./mvnw clean package -DskipTests

# Expose port (Render uses 10000 internally)
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/*.jar"]
