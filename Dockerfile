# Etapa 1: Construim aplicatia folosind Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Rulam aplicatia folosind Java
# AM SCHIMBAT AICI: Folosim eclipse-temurin care este stabil și valid
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]