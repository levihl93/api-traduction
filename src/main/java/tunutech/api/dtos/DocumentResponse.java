package tunutech.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tunutech.api.model.Document;
import tunutech.api.model.DocumentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private Long id;
    private String originalName;
    private String storedName;
    private Long fileSize;
    private String contentType;
    private Long idProject;

    private String codeProject;
    private String code;
    private Long idclient;
    private String sourceLanguage;
    private DocumentStatus status;
    private LocalDateTime uploadDate;
    private String message;
    private boolean success;

    public static DocumentResponse success(Document doc, String message) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .originalName(doc.getOriginalName())
                .storedName(doc.getStoredName())
                .fileSize(doc.getFileSize())
                .contentType(doc.getContentType())
                .idProject(doc.getProject().getId())
                .codeProject(doc.getProject().getCode())
                .status(doc.getStatus())
                .uploadDate(doc.getUploadDate())
                .message(message)
                .success(true)
                .build();
    }

    public static DocumentResponse error(String message) {
        return DocumentResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}
