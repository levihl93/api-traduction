# Dockerfile pour Spring Boot avec correction d'encodage
# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 1. Copier les fichiers Maven en premier (optimisation du cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 2. Télécharger les dépendances (optimisation du cache)
RUN mvn dependency:go-offline -B

# 3. Copier le code source
COPY src ./src

# 4. Configurer l'environnement UTF-8
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# 5. Build l'application avec encodage explicite
RUN mvn clean package -DskipTests \
    -Dfile.encoding=UTF-8 \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dproject.reporting.outputEncoding=UTF-8

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 6. Copier le JAR depuis le stage de build
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar app.jar

# 7. Créer les répertoires pour le stockage de fichiers
RUN mkdir -p /tmp/documents /tmp/documents/avatars /tmp/uploads

# 8. Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=9090
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -XX:+UseContainerSupport"

# 9. Exposer le port
EXPOSE 9090

# 10. Commande d'exécution
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]