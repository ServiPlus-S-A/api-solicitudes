# ── ETAPA 1: Construcción (Builder)
FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /build

# Copiar archivos de configuración de Gradle
COPY gradlew settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# Corregir saltos de línea de Windows si los hay y dar permisos
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Copiar el archivo de reglas de Checkstyle obligatorio
COPY checkstyle.xml .

# Copiar código fuente y compilar el JAR de producción sin ejecutar tests
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# ── ETAPA 2: Ejecución (Runtime Ligero y Seguro)
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache wget && addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copiar el artefacto desde la etapa de compilación
COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar

# Cambiar al usuario seguro sin privilegios de Root
USER appuser

# CAMBIO DE SEGURIDAD: Usamos un puerto no estándar para Spring Boot
ENV SERVER_PORT=9180
EXPOSE 9180

# Parámetros de optimización de memoria para contenedores
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]