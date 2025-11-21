package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Traducteur;

import javax.swing.*;
import java.util.List;

public interface TraducteurRepository extends JpaRepository<Traducteur,Long> {

    List<Traducteur> findByPresent(Boolean present);
    List<Traducteur> findByActive(Boolean active);
    Traducteur findByEmail(String email);
    List<Traducteur> findByPresentAndActive(Boolean present, Boolean active);
    Integer countByPresent(Boolean present);
}
