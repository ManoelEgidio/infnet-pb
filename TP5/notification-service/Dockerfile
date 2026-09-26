# ==========================================
# Multi-Stage Dockerfile for Notification-Service (TP5)
# Stage 1: Build with JDK 21 Alpine
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy build definition and wrapper
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Fetch dependencies to cache layers
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy source code and produce artifact
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# ==========================================
# Stage 2: Lightweight Production Runtime (JRE 21)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security best practice: non-root user execution
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built artifact from builder
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Expose HTTP port
EXPOSE 8082

# Container healthcheck using Actuator liveness probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health/liveness || exit 1

# Production JVM Flags & Active Profile
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
