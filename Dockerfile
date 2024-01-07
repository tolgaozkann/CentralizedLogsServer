# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package

# Stage 2: Create the final runtime image
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/target/LogServer-1.0-SNAPSHOT.jar /app/app.jar

# Expose the port the application runs on
EXPOSE ${PORT}

# Run the application
CMD ["java", "-jar", "app.jar"]
