# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace/app

COPY . .

RUN mvn install -DskipTests=true && \
mvn package -DskipTests=true

# Run stage
FROM gcr.io/distroless/java21-debian12:nonroot

EXPOSE 8080

VOLUME /tmp

# Copy the built JAR from the build stage
COPY --from=build /workspace/app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]