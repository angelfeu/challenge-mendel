# Stage 1: resolve and cache dependencies
FROM eclipse-temurin:25-jdk-alpine AS deps
WORKDIR /app
COPY gradle gradle
COPY gradlew settings.gradle build.gradle ./
RUN ./gradlew dependencies --no-daemon --console=plain

# Stage 2: build the jar
FROM deps AS builder
COPY src src
RUN ./gradlew bootJar --no-daemon --console=plain -x test

# Stage 3: minimal runtime image
FROM eclipse-temurin:25-jre-alpine AS runtime
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
