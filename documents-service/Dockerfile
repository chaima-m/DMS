FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built jar (run: mvn clean package -DskipTests first)
COPY target/documents-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
