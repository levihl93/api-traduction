package tunutech.api.exception;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(Long activityId) {
        super("Activité non trouvée avec l'ID: " + activityId);
    }
}
