package tunutech.api.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import tunutech.api.services.JwtService;

import java.security.Principal;
import java.util.Collections;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    public WebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() != null) {
            logger.info("WebSocket command: {}", accessor.getCommand());

            switch (accessor.getCommand()) {
                case CONNECT:
                    String token = extractToken(accessor);
                    logger.info("Token extracted: {}", token != null ? "YES" : "NO");

                    if (token != null && jwtService.isTokenValidForWebSocket(token)) {
                        String username = jwtService.extractUsername(token);
                        logger.info("User authenticated: {}", username);

                        // ✅ CORRECTION : Utiliser UsernamePasswordAuthenticationToken
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                        accessor.setUser(auth);

                    } else {
                        logger.error("Token validation failed");
                        throw new AuthenticationCredentialsNotFoundException("Token JWT invalide");
                    }
                    break;

                case SEND:
                    // ✅ CORRECTION : Afficher correctement le nom d'utilisateur
                    Object user = accessor.getUser();
                    String userName = "Unknown";
                    if (user instanceof Principal) {
                        userName = ((Principal) user).getName();
                    } else if (user instanceof UsernamePasswordAuthenticationToken) {
                        userName = ((UsernamePasswordAuthenticationToken) user).getName();
                    }
                    logger.info("SEND command - User: {}", userName);
                    break;
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}