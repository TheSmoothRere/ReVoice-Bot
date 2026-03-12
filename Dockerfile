# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy only files needed to resolve dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# PRE-BUILD: Download dependencies (this layer is only re-run if build.gradle.kts changes)
RUN ./gradlew dependencies --no-daemon

# Now copy the source code and build the final JAR
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# 2. Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Best practice: Run as a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]