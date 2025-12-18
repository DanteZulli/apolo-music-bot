# Stage 1: Build
FROM docker.io/eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy maven wrapper and pom first to cache dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execution rights on the maven wrapper
RUN chmod +x mvnw

# Download dependencies (go-offline) to leverage docker cache
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM docker.io/eclipse-temurin:25-jre

WORKDIR /app

# Create a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built jar from stage 1
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
