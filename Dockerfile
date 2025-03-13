FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn

COPY src src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*-fat.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
