# ==========================================
# Stage 1: Build JAR with Maven & Java 17
# ==========================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Cache dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source and compile
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==========================================
# Stage 2: Production JRE 17 Runtime
# ==========================================
FROM eclipse-temurin:17-jre

LABEL maintainer="DakPion Engineering"
LABEL project="DakPion Nostalgic Digital Postbox"

WORKDIR /app

# Create non-root application user
RUN groupadd -r dakpion && useradd -r -g dakpion dakpion

# Copy built JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar
RUN chown -R dakpion:dakpion /app

USER dakpion

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE="prod"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]