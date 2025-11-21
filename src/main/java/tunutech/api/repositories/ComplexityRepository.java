package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Complexity;
import tunutech.api.model.ProjectComplexity;

public interface ComplexityRepository extends JpaRepository<Complexity, Long> {

    Complexity findByProjectComplexity(ProjectComplexity projectComplexity);
}
