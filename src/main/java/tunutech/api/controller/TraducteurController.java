package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.*;
import tunutech.api.model.*;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.*;
import tunutech.api.Utils.Functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/translator/")
public class TraducteurController {
    @Autowired
    private TraducteurService traducteurService;
    @Autowired
    private TraducteurRepository traducteurRepository;
    @Autowired
    private  AuthenticationService authenticationService;
    @Autowired
    private UserEnableService userEnableService;

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Functions functions;
    @Autowired
    private UserService userService;
    @GetMapping("all")
public List<Traducteur> getallTraducteursPresentandActive(){return traducteurService.getAllPresentAndActive(); }

    @GetMapping("getUnique/{idtranslator}")
    public ResponseEntity<?> getInfo(@PathVariable Long idtranslator) throws Exception {
    Traducteur traducteur=traducteurService.getUnique(idtranslator);
    return ResponseEntity.ok(traducteur);
    }
    @GetMapping("allpresent")
public List<Traducteur> getallTraducteursPresents(){
        return traducteurService.getAllPresent();
    }
    @GetMapping("allactive")
public List<Traducteur> getallTraducteursActive(){
        return traducteurService.getAllActive();
    }

    @GetMapping("allactiveresponse")
public ResponseEntity<?> getallTraducteursActiveResponse(){
        return ResponseEntity.ok(
                traducteurService.getAllActive()
                        .stream()
                        .map(traducteur -> traducteurService.maptraducteur(traducteur, false))
                        .toList()
        );
    }

    @PostMapping("allavailableresponse")
    public ResponseEntity<?> getallTraducteursAvailableResponse(
            @RequestBody LanguerequestDto request) {

        List<Langue> langueList = request.getLangueslist();

        return ResponseEntity.ok(
                traducteurService.getAllDisponible(langueList)
                        .stream()
                        .map(traducteur -> traducteurService.maptraducteur(traducteur, false))
                        .toList()
        );
    }

    @GetMapping("alltraducteur")
    public List<Traducteur> getallTraducteurs(){return traducteurService.getAllTraducteurs(); }

    @PostMapping("add")
    public ResponseEntity<?>createTraducteur(@RequestBody TraducteurDto traducteurDto)
    {
        try{
            if(traducteurService.ifClientisPresent(traducteurDto.getEmail()))
            {
                Map<String, String> error = new HashMap<>();
                error.put("error", "l'adresse email existe déjà");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
           Traducteur traducteur=traducteurService.saveTraducteur(traducteurDto);

            RegisterUserDto registerUserDto=new RegisterUserDto();
            registerUserDto.setEmail(traducteurDto.getEmail());
            String pass=functions.generateComplexPassword(12,true);
            registerUserDto.setPassword(pass);
            Traducteur traducteur1=traducteurService.getbyEmailbyForce(traducteurDto.getEmail());
            registerUserDto.setIdtraducteur(traducteur1.getId());
            registerUserDto.setRoleUser(RoleUser.TRANSLATOR);

            // Créer l’utilisateur
            User registeredUser = authenticationService.signup(registerUserDto);

            //Enregistrer l'activite
            try {
                activityService.logUserActivity(registeredUser,ActivityType.USER_REGISTERED,"Traducteur");
            }catch (Exception e)
            {
                throw  new RuntimeException(e.getMessage());
            }
            //creation du token
            UserEnableToken userEnableToken=userEnableService.createToken(registeredUser);

            String resetLink = "http://localhost:3000/api/translator/enable?token=" + userEnableToken.getToken();
            System.out.println(pass);
            String passwordLink = pass;

            return ResponseEntity.ok(Map.of(
                    "message", "Lien d'activation envoyé à " + traducteur.getEmail(),
                    "enableLink", resetLink,
                    "passLink", pass
            ));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PatchMapping("setpresent")
    public ResponseEntity<?>setPresent(@RequestBody TraducteurDto traducteurDto) throws Exception {
        Traducteur traducteur=traducteurService.getUnique(traducteurDto.getId());
        traducteur.setPresent(traducteurDto.getPresent());
        System.out.println(traducteur.getEmail());
        traducteurRepository.save(traducteur);
        User user=userService.getByEmailByForce(traducteur.getEmail());
        user.setPresent(traducteurDto.getPresent());
        userRepository.save(user);
        //return ResponseEntity.ok(user.getEmail());
        return ResponseEntity.ok("Opération effectué avec succès");
    }
    @PatchMapping("setactif")
    public ResponseEntity<?>setActif(@RequestBody TraducteurDto traducteurDto) throws Exception {
        Traducteur traducteur=traducteurService.getUnique(traducteurDto.getId());
        traducteur.setActive(traducteurDto.getActive());
        traducteurRepository.save(traducteur);
        User user=userService.getByEmailByForce(traducteur.getEmail());
        user.setActive(traducteurDto.getActive());
        userRepository.save(user);
       // return ResponseEntity.ok(user.getEmail());
        return ResponseEntity.ok("Opération effectué avec succès");
    }
    @PatchMapping("setavailable")
    public ResponseEntity<?>setAvailable(@RequestBody TraducteurDto traducteurDto) throws Exception {
        Traducteur traducteur=traducteurService.getUnique(traducteurDto.getId());
        traducteur.setAvailable(traducteurDto.getAvailable());
        traducteurRepository.save(traducteur);
        return ResponseEntity.ok("Opération effectué avec succès");
    }
}
