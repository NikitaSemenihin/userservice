FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY target/userservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]