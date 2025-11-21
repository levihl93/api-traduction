package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetLangueCible;
import tunutech.api.model.ProjetLangueSource;

import java.util.List;
import java.util.Optional;

public interface ProjetLangueSourceRepository extends JpaRepository<ProjetLangueSource, Long> {
    List<ProjetLangueSource> findByProjectId(Long projectId);
    Optional<ProjetLangueSource> findByProjectIdAndLangueId(Long projectId, Long langueId);
    void  deleteAllByProject(Project project);
}
