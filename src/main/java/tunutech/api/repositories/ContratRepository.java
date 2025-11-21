package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Contrat;

import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    Optional<Contrat> findByProjectId(Long projetId);
    List<Contrat> findByContratStatut(String statut);
    void deleteByProjectId(long projectId);
}
