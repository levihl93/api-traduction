package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.PasswordResetToken;
import tunutech.api.model.UserEnableToken;

import java.util.Optional;

public interface UserEnableTokenRepository extends JpaRepository<UserEnableToken, Long> {
    Optional<UserEnableToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
