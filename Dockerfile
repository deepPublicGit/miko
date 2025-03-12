# Stage 1: Build the application
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
#RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw clean package -DskipTests

## Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*-fat.jar app.jar

# Expose port 8080 for users (maps to internal port 8888)
EXPOSE 8080

# Run the application and bind it to the correct port
ENTRYPOINT ["java", "-jar", "app.jar"]
