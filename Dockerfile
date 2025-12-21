# Étape 1 : builder le JAR avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : image légère pour exécuter le JAR
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copier le JAR généré depuis l'étape build
COPY --from=build /app/target/smartshop-0.0.1-SNAPSHOT.jar app.jar

# Le backend écoute sur le port 8080
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]