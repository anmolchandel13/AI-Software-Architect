# =============================================================
# Build Stage — Compiles and packages the Spring Boot JAR
# =============================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy the pom.xml and dependency definitions first (caching layer)
COPY pom.xml .

# Download dependencies (keeps builds fast if code changes but dependencies don't)
RUN mvn dependency:go-offline -B

# Copy the source code and build the package
COPY src ./src
RUN mvn clean package -DskipTests

# =============================================================
# Run Stage — Lightweight JRE image to run the compiled JAR
# =============================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /app/target/ai-software-architect-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose standard port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
