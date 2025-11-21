package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.ProjetTraducteur;

import java.util.List;
import java.util.Optional;

public interface ProjetTraducteurRepository extends JpaRepository<ProjetTraducteur, Long> {
   Optional <ProjetTraducteur> findByProjectId(Long id);

   List<ProjetTraducteur> findByTraducteurId(Long traductuerid);

   Optional<ProjetTraducteur>findByTraducteurIdAndProjectId(Long traducteurid, long projetid);
}
