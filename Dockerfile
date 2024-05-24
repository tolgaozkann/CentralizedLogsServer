# Stage 1: Build the frontend
FROM node:20 AS frontend-build
WORKDIR /app/Dashboard

# Copy the frontend source code
COPY Dashboard/package*.json ./
RUN npm install

# Copy the rest of the frontend files
COPY Dashboard/ ./
RUN npm run build

# Stage 2: Build the backend
FROM maven:3.9.6-eclipse-temurin-21-jammy AS backend-build
WORKDIR /app

# Copy the backend project files
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 3: Create the final runtime image
FROM openjdk:21-jdk-slim
WORKDIR /app

# Install Node.js 18
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# Copy the built backend application from the backend build stage
COPY --from=backend-build /app/target/LogServer-1.0-SNAPSHOT.jar /app/app.jar

# Copy the built frontend from the frontend build stage
COPY --from=frontend-build /app/Dashboard/ /app/Dashboard/

# Copy the .env file (ensure it exists in your Docker context)
COPY .env /app/

# Install dependencies for the frontend
RUN cd Dashboard && npm install

# Expose the ports the applications run on
EXPOSE 8888
EXPOSE 3000

CMD ["sh", "-c", "java -jar app.jar & cd Dashboard && PORT=3000 npm start"]
