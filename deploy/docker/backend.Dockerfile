# Backend image (API only; the UI is served by the nginx frontend image).
# Build context: repository root (see docker-compose.yml).

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY backend/pom.xml .
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app \
    && mkdir -p /data && chown app:app /data
COPY --from=build /src/target/learning-tracker.jar /app/learning-tracker.jar
USER app
ENV APP_DATA_DIR=/data \
    SERVER_PORT=8080 \
    JAVA_TOOL_OPTIONS="-Xmx256m"
VOLUME /data
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "/app/learning-tracker.jar"]
