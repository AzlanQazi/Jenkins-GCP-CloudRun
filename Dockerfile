FROM eclipse-temurin:21-jre
# openjdk:21-jre-slim


# 1. Create a non-root user for security
RUN useradd -ms /bin/sh appuser


WORKDIR /app

COPY target/hello-world-1.0-SNAPSHOT.jar /app/hello-world.jar


# 4. Change ownership of the app directory to the new user
RUN chown -R appuser:appuser /app
# 5. Switch to the non-root user
USER appuser


EXPOSE 8090
# Add the server parameter if you don't use Springboot default port 8080
CMD ["java", "-jar", "hello-world.jar", "--server.port=8090"]
