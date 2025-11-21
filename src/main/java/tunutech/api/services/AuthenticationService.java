package tunutech.api.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.LoginUserDto;
import tunutech.api.dtos.RegisterUserDto;
import tunutech.api.model.Client;
import tunutech.api.model.Traducteur;
import tunutech.api.model.User;
import tunutech.api.repositories.ClientRepository;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.repositories.UserRepository;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    @Autowired
    private TraducteurRepository traducteurRepository;

    @Autowired
    private ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final  JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        if(input != null && input.getIdclient() != null)
        {
            Optional <Client> client=clientRepository.findById(input.getIdclient());
            if(client.isPresent())
            {
                user.setClient(client.get());
            }
        }
        if(input != null && input.getIdtraducteur() != null)
        {
            Optional <Traducteur> traducteur=traducteurRepository.findById(input.getIdtraducteur());
            if(traducteur.isPresent())
            {
                System.out.println("traductor");
                user.setTraducteur(traducteur.get());
            }
        }
        user.setRoleUser(input.getRoleUser());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public String refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (jwtService.isTokenValid(refreshToken, user)) {
            return jwtService.generateToken(user); // nouveau access token
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    public Optional<User> loadByemail(String email) {
        return userRepository.findByEmail(email);
    }
}
