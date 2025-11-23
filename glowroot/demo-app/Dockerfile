# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
COPY glowroot.jar .
COPY glowroot.properties .
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:glowroot.jar", "-jar", "app.jar"]
