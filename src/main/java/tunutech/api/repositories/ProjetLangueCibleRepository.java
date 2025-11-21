package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetLangueCible;

import java.util.List;
import java.util.Optional;

public interface ProjetLangueCibleRepository extends JpaRepository<ProjetLangueCible, Long> {
    List<ProjetLangueCible> findByProjectId(Long idproject);
    Optional<ProjetLangueCible> findByProjectIdAndLangueId(Long idproject, Long idlangue);
    void  deleteAllByProject(Project project);
}
