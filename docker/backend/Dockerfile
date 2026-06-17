# syntax=docker/dockerfile:1

FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /build
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
COPY src ./src
RUN ./gradlew -q bootJar -x test

FROM eclipse-temurin:21.0.7_6-jre-alpine AS runtime
RUN apk add --no-cache wget && addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /build/build/libs/solicitudes-service-*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
