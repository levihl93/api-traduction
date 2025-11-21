package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
