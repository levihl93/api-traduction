package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTerminer(Boolean terminer);
    List<Project> findByValider(Boolean valider);
    List<Project>findByClientId(Long id);
    Project findByCode(String code);
    Boolean existsByCode(String code);
}
