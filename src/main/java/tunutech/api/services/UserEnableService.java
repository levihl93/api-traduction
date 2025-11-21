package tunutech.api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tunutech.api.model.PasswordResetToken;
import tunutech.api.model.Traducteur;
import tunutech.api.model.User;
import tunutech.api.model.UserEnableToken;
import tunutech.api.repositories.PasswordResetTokenRepository;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.repositories.UserEnableTokenRepository;
import tunutech.api.repositories.UserRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserEnableService {
    private final UserEnableTokenRepository tokenRepo;
    @Autowired
    private TraducteurRepository traducteurRepository;
    private final UserRepository userRepo;

    @Transactional
    public UserEnableToken createToken(User user) {
        // Supprimer anciens tokens
        tokenRepo.deleteByUserId(user.getId());

        UserEnableToken token =UserEnableToken.generate(user, 30);
        return tokenRepo.save(token);
    }

    public ResponseEntity<?> verifToken(String tokenValue) {
        try {
            /*System.out.println("=== DEBUG TOKEN ===");
            System.out.println("Token reçu: '" + tokenValue + "'");
            System.out.println("Longueur: " + tokenValue.length());*/

            // Recherche du token
            UserEnableToken token = tokenRepo.findByToken(tokenValue)
                    .orElseThrow(() -> {
                        //System.out.println("❌ Token NON trouvé en base");
                        return new RuntimeException("Token invalide");
                    });

           /* System.out.println("✅ Token trouvé: '" + token.getToken() + "'");
            System.out.println("ID: " + token.getId());
            System.out.println("Expire à: " + token.getExpiresAt());
            System.out.println("Maintenant: " + new Date());*/

            // Vérification expiration
            if (token.getExpiresAt().before(new Date())) {
                System.out.println("❌ Token EXPIRÉ");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token expiré");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            //System.out.println("✅ Token VALIDE");

            // ✅ Retournez une réponse JSON propre
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("valid", true);
            successResponse.put("message", "Token valide");
            successResponse.put("userId", token.getUser().getId());
            successResponse.put("email", token.getUser().getEmail()); // Si utile
            successResponse.put("expiresAt", token.getExpiresAt());

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            // Gestion des autres exceptions
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    public void EnableUser(String tokenValue) {
        UserEnableToken token = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (token.getExpiresAt().before(new Date())) {
            throw new RuntimeException("Token expiré");
        }

        User user = token.getUser();
        if(user.getTraducteur()!=null)
        {
            Traducteur traducteur= user.getTraducteur();
            traducteur.setActive(true);
            traducteurRepository.save(traducteur);
        }
        user.setActive(true);
        userRepo.save(user);

        tokenRepo.delete(token);
    }
}
