FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . .

WORKDIR /app/poultry-shop

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD sh -c "java -jar target/*.jar"
