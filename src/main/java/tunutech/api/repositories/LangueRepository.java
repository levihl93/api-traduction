package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Langue;

import java.util.List;
import java.util.Optional;

public interface LangueRepository extends JpaRepository<Langue, Long> {

    List<Langue> findByPresent(Boolean present);
    List<Langue> findByPresentOrderByName(Boolean present);
    Optional <Langue> findByName(String name);
   Optional <Langue> findByCode(String code);
    List<Langue> findByActive(Boolean active);
}
