FROM openjdk:21-jre-slim

# Install curl for health checks (Ubuntu/Debian commands)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Create non-root user for security (Ubuntu/Debian commands)
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the JAR file from Gradle build directory
COPY build/libs/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose the port that the application will run on
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]