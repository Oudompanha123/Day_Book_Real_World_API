# ---- build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---- run ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --no-create-home daybook
COPY --from=build /app/target/daybook-api-1.0.0.jar app.jar
USER daybook
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
