# Use the official Maven image as the build environment
FROM maven:3.8.3-openjdk-17-slim AS build

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and src directories to the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image as the runtime environment
FROM openjdk:17-slim

# Set the working directory to /app
WORKDIR /app

# Copy the built JAR file from the build environment to the container
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "app.jar"]
