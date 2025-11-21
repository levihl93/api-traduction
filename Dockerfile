FROM eclipse-temurin:17-jdk-jammy
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]