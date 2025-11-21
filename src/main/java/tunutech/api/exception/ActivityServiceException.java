package tunutech.api.exception;

public class ActivityServiceException extends RuntimeException {
    public ActivityServiceException(String message) {
        super(message);
    }
    public ActivityServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
