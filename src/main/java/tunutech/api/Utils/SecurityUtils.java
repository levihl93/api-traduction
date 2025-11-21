package tunutech.api.Utils;

import org.springframework.stereotype.Component;
import tunutech.api.model.RoleUser;
import tunutech.api.model.User;

@Component
public class SecurityUtils {
    public static String safeGetUserFullName(User user) {
        if (user == null) {
            System.out.println("⚠️ User est null dans safeGetUserFullName");
            return "Utilisateur inconnu";
        }

        System.out.println("🔍 safeGetUserFullName - User: " + user.getEmail() + ", Role: " + user.getRoleUser());

        try {
            if (user.getRoleUser() == RoleUser.CLIENT) {
                System.out.println("🔍 Client: " + (user.getClient() != null ? "présent" : "null"));
                return user.getClient() != null ? user.getClient().getFullName() : "Client";
            } else if (user.getRoleUser() == RoleUser.TRANSLATOR) {
                System.out.println("🔍 Traducteur: " + (user.getTraducteur() != null ? "présent" : "null"));
                return user.getTraducteur() != null ? user.getTraducteur().getFullName() : "Traducteur";
            } else if (user.getRoleUser() == RoleUser.ADMIN) {
                return "Administrateur";
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur safeGetUserFullName: " + e.getMessage());
        }

        // Fallback vers l'email ou une valeur par défaut
        return user.getEmail() != null ? user.getEmail() : "Utilisateur";
    }
}
