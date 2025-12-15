# Dockerfile pour Spring Boot avec correction d'encodage
# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 1. Copier les fichiers Maven en premier (optimisation du cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 2. Définir l'encodage UTF-8 DÈS LE DÉBUT
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# 3. Télécharger les dépendances avec encodage
RUN mvn dependency:go-offline -B \
    -Dfile.encoding=UTF-8 \
    -Dproject.build.sourceEncoding=UTF-8

# 4. Copier le code source
COPY src ./src

# 5. Vérifier l'encodage des fichiers resources (debug optionnel)
# RUN find src/main/resources -type f -name "*.properties" -exec file -i {} \;

# 6. Build l'application avec TOUS les paramètres d'encodage
RUN mvn clean package -DskipTests \
    -Dfile.encoding=UTF-8 \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dproject.reporting.outputEncoding=UTF-8 \
    -Dresources.encoding=UTF-8 \
    -Dmaven.resources.encoding=UTF-8

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 7. Définir l'encodage dans le runtime aussi
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -XX:+UseContainerSupport"

# 8. Copier le JAR depuis le stage de build
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar app.jar

# 9. Créer les répertoires pour le stockage de fichiers
RUN mkdir -p /tmp/documents /tmp/documents/avatars /tmp/uploads

# 10. Variables d'environnement Spring
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=9090

# 11. Exposer le port
EXPOSE 9090

# 12. Commande d'exécution avec encodage explicite
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -Dspring.config.location=classpath:/application.properties -jar app.jar"]