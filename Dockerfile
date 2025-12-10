# Utilisez une image plus légère
FROM eclipse-temurin:17-jre-jammy AS builder

WORKDIR /app

# 1. Copier d'abord les fichiers Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 2. Donner les permissions et télécharger les dépendances
RUN chmod +x ./mvnw && \
    ./mvnw dependency:go-offline -B

# 3. Copier le code source
COPY src ./src

# 4. Fixer les problèmes d'encodage
# Installer les outils nécessaires
RUN apt-get update && \
    apt-get install -y dos2unix locales && \
    rm -rf /var/lib/apt/lists/*

# Configurer l'UTF-8
RUN sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# Convertir les fichiers properties
RUN find /app -name "*.properties" -type f -exec dos2unix {} \; 2>/dev/null || true

# 5. Construire avec l'encodage UTF-8
RUN ./mvnw clean package -DskipTests \
    -Dfile.encoding=UTF-8 \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dproject.reporting.outputEncoding=UTF-8

# Image finale légère
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

# Créer les répertoires pour le stockage de fichiers
RUN mkdir -p /tmp/documents /tmp/documents/avatars /tmp/uploads

# Exposer le port
EXPOSE 9090

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""
ENV PORT=9090

# Commande d'exécution
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dfile.encoding=UTF-8 -Dserver.port=${PORT} -jar app.jar"]