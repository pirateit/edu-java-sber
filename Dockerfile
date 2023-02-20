FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/main-0.0.1-SNAPSHOT.jar inventory.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "inventory.jar"]
