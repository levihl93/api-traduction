package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tunutech.api.model.TraducteurLangue;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraducteurLangueRepository extends JpaRepository<TraducteurLangue, Long> {
    List<TraducteurLangue> findByTraducteurId(Long idtraducteur);
    List<TraducteurLangue> findByLangueId(Long idlangue);

    Optional <TraducteurLangue> findByTraducteurIdAndLangueId(Long idtraducteur,Long idlangue);
}
