package tunutech.api.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tunutech.api.model.RoleUser;
import tunutech.api.model.Traducteur;
import tunutech.api.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    // CORRECTION : findByActive (pas findbyActive)
    List<User> findByActive(Boolean active);

    // CORRECTION : findByRoleUser (pas findbyRoleUser)
    List<User> findByRoleUser(RoleUser roleUser);

    // Méthodes supplémentaires utiles pour les notifications
    List<User> findByRoleUserAndActive(RoleUser roleUser, Boolean active);

    Optional<User>findByTraducteurId(Long id);
    Optional<User>findByClientId(Long id);

    Optional<String>findAvatarUrlById(Long id);

}
