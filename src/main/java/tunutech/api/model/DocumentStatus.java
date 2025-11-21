package tunutech.api.model;

public enum DocumentStatus {
    // États principaux
    UPLOADED("Téléchargé"),           // Document uploadé mais pas encore traité
    IN_TRANSLATION("En traduction"),  // En cours de traduction
    TRANSLATED("Traduit"),            // Traduction terminée
    REVIEW("En révision"),            // En cours de révision
    COMPLETED("Terminé"),            // Processus complet
    ERROR("Erreur"),                  // Erreur lors du traitement
    CANCELLED("Annulé"),              // Processus annulé
    ARCHIVED("Archivé");              // Document archivé

    private final String displayName;

    DocumentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
