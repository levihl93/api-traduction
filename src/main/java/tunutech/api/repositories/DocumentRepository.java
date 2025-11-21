package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Document;
import tunutech.api.model.Project;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Document findByProjectId(Long idproject);
    List<Document> findDocumentsByProjectId(Long idproject);
    void deleteByProject(Project project);
}
