package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.*;
import tunutech.api.model.*;
import tunutech.api.repositories.ClientRepository;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    @Autowired
    private ClientService clientService;
    private final AuthenticationService authenticationService;
    private final PasswordResetService resetService;
    @Autowired
    private UserEnableService userEnableService;
    @Autowired
    private TraducteurRepository traducteurRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;


    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, PasswordResetService resetService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.resetService = resetService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        // Vérifier si l'email existe déjà
        if (authenticationService.loadByemail(registerUserDto.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Un utilisateur avec cet email existe déjà !");
        }

        registerUserDto.setRoleUser(RoleUser.CLIENT);
        // Créer l’utilisateur
        User registeredUser = authenticationService.signup(registerUserDto);

        // Mapper vers un DTO pour ne pas exposer le password
        UserDto userDto = new UserDto();
        userDto.setId(registeredUser.getId());
        userDto.setEmail(registeredUser.getEmail());
        userDto.setAdmin(registerUserDto.isAdmin());
        if (registeredUser.getTraducteur() != null) {
            userDto.setIdTraducteur(registeredUser.getTraducteur().getId());
        }
        if (registeredUser.getClient() != null) {
            userDto.setIdClient(registeredUser.getClient().getId());
        }

        return ResponseEntity.ok(userDto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDto loginUserDto) {

        if (!authenticationService.loadByemail(loginUserDto.getEmail()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Utilisateur non reconnu");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            if(!authenticatedUser.isPresent())
            {
                Map<String, String> error = new HashMap<>();
                error.put("error", "your account is deleted");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
            if(!authenticatedUser.isActive())
            {
                Map<String, String> error = new HashMap<>();
                error.put("error", "your account is not actived");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
            UserDto userDto = new UserDto();
            userDto.setId(authenticatedUser.getId());
            userDto.setEmail(authenticatedUser.getEmail());
            userDto.setAvatarType(authenticatedUser.getAvatarType());
            userDto.setAvatarInitialColor(authenticatedUser.getAvatarInitialColor());
            userDto.setAvatarUrl(authenticatedUser.getAvatarUrl());

            if (authenticatedUser.getRoleUser()==RoleUser.ADMIN) {
                userDto.setAdmin(true);
            }
            if (authenticatedUser.getTraducteur() != null) {
                if(authenticatedUser.getRoleUser()==RoleUser.ADMIN)
                {
                    userDto.setSenderRole(SenderRole.ADMIN);
                }else {
                    userDto.setSenderRole(SenderRole.TRANSLATOR);
                }
                Optional<Traducteur> traducteur=traducteurRepository.findById(authenticatedUser.getTraducteur().getId());
                userDto.setIdTraducteur(authenticatedUser.getTraducteur().getId());
                if(traducteur.isPresent())
                {
                    userDto.setName(traducteur.get().getFullName());
                }
            } else if (authenticatedUser.getClient() != null) {
                userDto.setSenderRole(SenderRole.CLIENT);
                Optional<Client>client=clientRepository.findById(authenticatedUser.getClient().getId());
                userDto.setIdClient(authenticatedUser.getClient().getId());
                if(client.isPresent())
                {
                    userDto.setName(client.get().getFullName());
                }

            }
            String accessToken = jwtService.generateToken(authenticatedUser);
            String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
            System.out.println("le user "+userDto.getAvatarUrl());
            LoginResponse loginResponse = new LoginResponse()
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .setExpiresIn(jwtService.getExpirationTime())
                    .setUserDto(userDto);

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Mot de pass incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (UsernameNotFoundException | NoSuchElementException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Une erreur est survenue");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }




    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken) {
        // 1. extraire l'email depuis le refresh token
        String email = jwtService.extractUsername(refreshToken);

        // 2. récupérer l'utilisateur depuis la DB
        Optional<User> userOptional = authenticationService.loadByemail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // ou un message "User not found"
        }

        User user = userOptional.get();

        // 3. vérifier la validité du refresh token
        if (!jwtService.isTokenValid(refreshToken, user)) {
            return ResponseEntity.badRequest().build();
        }

        // 4. générer un nouvel access token
        String newAccessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(
                new LoginResponse()
                        .setAccessToken(newAccessToken)
                        .setRefreshToken(refreshToken) // on garde le même refresh
                        .setExpiresIn(jwtService.getExpirationTime())
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email manquant")); // ← JSON
        }

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable")); // ← On change ici

            PasswordResetToken token = resetService.createToken(user);
            String resetLink = "http://localhost:3000/api/auth/password/update?token=" + token.getToken();

            return ResponseEntity.ok(Map.of(
                    "message", "Lien de réinitialisation envoyé à " + email,
                    "resetLink", resetLink
            ));

        } catch (RuntimeException e) {
            // Ici on retourne du JSON pour les erreurs aussi
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage())); // ← "Utilisateur introuvable" en JSON
        }
    }
    // AuthenticationController.java
    @PostMapping("/verif-token")
    public ResponseEntity<?> verifToken(@RequestBody TokenRequest request) {
        try {
            String tokenValue = request.getToken();

            // ✅ Appeler le service qui retourne ResponseEntity
            ResponseEntity<?> response = resetService.verifToken(tokenValue);

            return response;

        } catch (RuntimeException e) {
            // Gestion des erreurs inattendues
            Map<String, String> error = new HashMap<>();
            error.put("error", " " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        resetService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
    }

    @PostMapping("/set-password")
    public ResponseEntity<String> resetPasswordWithoutToken(
            @RequestParam Integer iduser,
            @RequestParam String newPassword) {
        resetService.resetPasswordWithoutToken(iduser,newPassword);
        return ResponseEntity.ok("Mot de passe défini avec succès");
    }

    @PostMapping("/addclient")
    public ResponseEntity<?>createClient(@RequestBody ClientDto clientDto)
    {
        try{
           if(clientService.ifClientisPresent(clientDto.getEmail()))
           {
               Map<String, String> error = new HashMap<>();
               error.put("error", "l'adresse email existe déjà");
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
           }
            Client client=clientService.saveClient(clientDto);

            RegisterUserDto registerUserDto=new RegisterUserDto();
            registerUserDto.setEmail(client.getEmail());
            registerUserDto.setPassword(clientDto.getPassword());
            registerUserDto.setRoleUser(RoleUser.CLIENT);
            Client leclient=clientService.getbyEmailbyForce(client.getEmail());

            registerUserDto.setIdclient(leclient.getId());

            // Créer l’utilisateur
            User registeredUser = authenticationService.signup(registerUserDto);
            //creation du token
            UserEnableToken userEnableToken=userEnableService.createToken(registeredUser);

            String resetLink = "http://localhost:3000/api/client/enable?token=" + userEnableToken.getToken();

            return ResponseEntity.ok(Map.of(
                    "message", "Lien d'activation envoyé à " + client.getEmail(),
                    "enableLink", resetLink
            ));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verif-token-user")
    public ResponseEntity<?> verifTokenUser(@RequestBody TokenRequest request) {
        try {
            String tokenValue = request.getToken();

            // ✅ Appeler le service qui retourne ResponseEntity
            ResponseEntity<?> response = userEnableService.verifToken(tokenValue);

            userEnableService.EnableUser(tokenValue);

            return response;

        } catch (RuntimeException e) {
            // Gestion des erreurs inattendues
            Map<String, String> error = new HashMap<>();
            error.put("error", " " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}