/*package tunutech.api.configs;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private EntityManager entityManager;

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void ensureProjectTableExists() {
        try {
            System.out.println("🔍 Vérification de la table project...");

            // Vérifier si la table project existe
            String checkSql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'project')";
            Boolean exists = (Boolean) entityManager.createNativeQuery(checkSql).getSingleResult();

            if (!exists) {
                System.out.println("🚀 Création de la table project...");

                String createSql = "CREATE TABLE project (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "code VARCHAR(255) NOT NULL UNIQUE, " +
                        "idclient BIGINT NOT NULL, " +
                        "valider BOOLEAN NOT NULL DEFAULT false, " +
                        "project_status VARCHAR(255) NOT NULL, " +
                        "type_document VARCHAR(255) NOT NULL, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "priority_type VARCHAR(255) NOT NULL, " +
                        "wordscount FLOAT NOT NULL, " +
                        "priceper_word FLOAT NOT NULL, " +
                        "estimated_price FLOAT NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "annuler BOOLEAN NOT NULL DEFAULT false, " +
                        "\"end\" BOOLEAN NOT NULL DEFAULT false, " +
                        "end_at TIMESTAMP, " +
                        "datevoulue DATE NOT NULL, " +
                        "created_at TIMESTAMP, " +
                        "updated_at TIMESTAMP" +
                        ")";

                entityManager.createNativeQuery(createSql).executeUpdate();
                System.out.println("✅ Table project créée avec succès");
            } else {
                System.out.println("✅ Table project existe déjà");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la vérification/création de project: " + e.getMessage());
            e.printStackTrace();
        }

        // Vérifier aussi chatroom
        ensureChatRoomTableExists();
    }

    @Transactional
    public void ensureChatRoomTableExists() {
        try {
            System.out.println("🔍 Vérification de la table chatroom...");

            String checkSql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'chatroom')";
            Boolean exists = (Boolean) entityManager.createNativeQuery(checkSql).getSingleResult();

            if (!exists) {
                System.out.println("🚀 Création de la table chatroom...");

                String createSql = "CREATE TABLE chatroom (" +
                        "id BIGSERIAL PRIMARY KEY, " +
                        "room_id VARCHAR(255) NOT NULL UNIQUE, " +
                        "\"end\" BOOLEAN NOT NULL DEFAULT false, " +
                        "end_at TIMESTAMP, " +
                        "idclient BIGINT, " +
                        "idtranslator BIGINT, " +
                        "idtranslator2 BIGINT, " +
                        "idproject BIGINT NOT NULL, " +
                        "chat_status VARCHAR(255) NOT NULL, " +
                        "created_at TIMESTAMP" +
                        ")";

                entityManager.createNativeQuery(createSql).executeUpdate();
                System.out.println("✅ Table chatroom créée avec succès");

                // Ajouter les contraintes étrangères
                addForeignKeys();
            } else {
                System.out.println("✅ Table chatroom existe déjà");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la vérification/création de chatroom: " + e.getMessage());
        }
    }

    @Transactional
    public void addForeignKeys() {
        try {
            System.out.println("🔗 Ajout des contraintes étrangères...");

            // Attendre un peu que les tables soient prêtes
            Thread.sleep(1000);

            // Contrainte vers project
            String fkProject = "ALTER TABLE chatroom ADD CONSTRAINT fk_chatroom_project FOREIGN KEY (idproject) REFERENCES project(id)";
            entityManager.createNativeQuery(fkProject).executeUpdate();

            // Contrainte vers client
            String fkClient = "ALTER TABLE chatroom ADD CONSTRAINT fk_chatroom_client FOREIGN KEY (idclient) REFERENCES client(id)";
            entityManager.createNativeQuery(fkClient).executeUpdate();

            System.out.println("✅ Contraintes étrangères ajoutées avec succès");

        } catch (Exception e) {
            System.out.println("❌ Erreur ajout contraintes: " + e.getMessage());
        }
    }
}*/