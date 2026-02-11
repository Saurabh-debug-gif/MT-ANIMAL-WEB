FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy everything
COPY . .

# Go into project folder
WORKDIR /app/poultry-shop

# ✅ Fix permissions for mvnw
RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/*.jar"]
