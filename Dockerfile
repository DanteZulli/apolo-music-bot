# Stage 1: Build
FROM docker.io/eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy gradle wrapper and build scripts first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY settings.gradle build.gradle gradle.properties .

# Grant execution rights on the gradle wrapper
RUN chmod +x gradlew

# Download dependencies to leverage docker cache
RUN ./gradlew dependencies --no-daemon

# Copy source code and build
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# Stage 2: Runtime
FROM docker.io/eclipse-temurin:25-jre

WORKDIR /app

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built jar from stage 1
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
