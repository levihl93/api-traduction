package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Client;
import tunutech.api.model.Traducteur;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
  Optional <Client> findByEmail(String email);
  List<Client> findByPresent(Boolean present);
}
