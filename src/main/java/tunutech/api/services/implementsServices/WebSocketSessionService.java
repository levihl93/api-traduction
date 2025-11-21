package tunutech.api.services.implementsServices;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionService {
    // ✅ CORRECTION : Spécifier les types Long et Set<String>
    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<Long, Set<String>>();



    public void addUserSession(Long userId, String sessionId) {
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void removeUserSession(Long userId, String sessionId) {
        Set<String> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    public Set<String> getUserSessions(Long userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    // Méthode utilitaire pour savoir si un user est connecté
    public boolean isUserConnected(Long userId) {
        return userSessions.containsKey(userId) && !userSessions.get(userId).isEmpty();
    }
}
