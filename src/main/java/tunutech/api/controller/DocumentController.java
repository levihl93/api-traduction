package tunutech.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tunutech.api.model.Document;
import tunutech.api.model.Project;
import tunutech.api.services.DocumentService;
import tunutech.api.services.FileStorageConfig;
import tunutech.api.services.ProjetService;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {
    private final DocumentService documentService;

    @Autowired
    private ProjetService projetService;

    @GetMapping("/{documentId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Resource resource = documentService.downloadDocument(documentId);
            Document document = documentService.findById(documentId).orElseThrow();

            // Vérifier que c'est bien un fichier
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null); // Ou retourner une erreur propre
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getOriginalName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document") // Forcer le type Word
                    .body(resource);

        } catch (Exception e) {
            // NE PAS retourner du JSON ici
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain")
                    .body(null);
        }
    }


    private String determineContentType(Document document) {
        // Utiliser le contentType stocké ou le deviner depuis l'extension
        if (document.getContentType() != null && !document.getContentType().isEmpty()) {
            return document.getContentType();
        }

        // Fallback: deviner depuis le nom du fichier
        String fileName = document.getOriginalName().toLowerCase();
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (fileName.endsWith(".doc")) {
            return "application/msword";
        } else if (fileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }

    // Upload de document (pour compléter)
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("wordscount") Float wordsCount,
            @RequestParam("projectId") Long projectId) {
        try {
            Project project=projetService.getUniquebyId(projectId);
            Document savedDocument = documentService. storeDocument(file, project,"dddd",wordsCount);

            return ResponseEntity.ok(savedDocument);
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload: " + e.getMessage());
        }
    }

    // Liste des documents par projet
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<Document>> getProjectDocuments(@PathVariable Long projectId) {
        try {
            List<Document> documents = documentService.ListbyIdProject(projectId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération documents: " + e.getMessage());
        }
    }
}
