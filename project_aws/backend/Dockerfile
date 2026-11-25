# Multi-stage build: Maven only in the build stage, small JRE image at runtime (ECR/ECS in Phase 8)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/api.jar app.jar
EXPOSE 8080
# MaxRAMPercentage: respect the container memory limit (Fargate task size)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
