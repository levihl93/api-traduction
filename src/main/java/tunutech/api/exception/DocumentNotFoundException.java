package tunutech.api.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends  RuntimeException{
    public DocumentNotFoundException() {
        super("Document non trouvé");
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public DocumentNotFoundException(Long documentId) {
        super("Document non trouvé avec l'ID: " + documentId);
    }
}
