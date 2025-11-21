package tunutech.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.ProfilDto;
import tunutech.api.dtos.TraducteurDto;
import tunutech.api.dtos.UserDto;
import tunutech.api.model.Client;
import tunutech.api.model.Traducteur;
import tunutech.api.model.User;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.ClientService;
import tunutech.api.services.TraducteurService;
import tunutech.api.services.UserService;

import java.util.List;

@RestController
@RequestMapping( "/users")
public class UserController {
    private final UserService userService;
    @Autowired
    private TraducteurService traducteurService;
    @Autowired
    private ClientService clientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraducteurRepository traducteurRepository;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }

    /*@GetMapping(value = "/profil")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }*/

    @GetMapping("/profil")
    public ResponseEntity<UserDto> authenticatedUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @GetMapping("getinfoprofil/{iduser}")
    public ResponseEntity<?> getInfo(@PathVariable Integer iduser) throws Exception {
        UserDto userDto=userService.getCurrentUserProfile();
        User user=userService.getUnique(Long.valueOf((iduser)));
        ProfilDto profilDto=new ProfilDto();
        profilDto.setRoleUser(user.getRoleUser());
        profilDto.setId(user.getId());
        profilDto.setActif(user.isActive());

        if(user.getClient()==null)
        {
            if(user.getTraducteur()!=null)
            {
                Traducteur traducteur=traducteurService.getUnique(user.getTraducteur().getId());
                profilDto.setEmail(traducteur.getEmail());
                profilDto.setSexe(traducteur.getSexe());
                profilDto.setIdentite(traducteur.getFirstname()+" "+traducteur.getLastname());
                profilDto.setNom(traducteur.getLastname());
                profilDto.setPrenoms(traducteur.getFirstname());
                profilDto.setTraducteur(traducteur);
                profilDto.setPays(traducteur.getPays());
                profilDto.setAdresse(traducteur.getAdresse());
                profilDto.setTelephone(traducteur.getTelephone());
                profilDto.setJoindate(traducteur.getCreated_At());
                profilDto.setType("physical");
            }
        }else {
            if(user.getTraducteur()==null)
            {
                Client client=clientService.getUnique(user.getClient().getId());
                profilDto.setEmail(client.getEmail());
                profilDto.setSexe(client.getSexe());
                profilDto.setIdentite(client.getFirstname()+" "+client.getLastname());
                profilDto.setPays(client.getPays());
                profilDto.setAdresse(client.getAdresse());
                profilDto.setNom(client.getLastname());
                profilDto.setPrenoms(client.getFirstname());
                profilDto.setClient(client);
                profilDto.setDenomination(client.getDenomination());
                profilDto.setSecteur(client.getSecteur());
                profilDto.setTelephone(client.getTelephone());
                profilDto.setJoindate(client.getCreated_At());
                profilDto.setType("physical");
                if(client.getDenomination()!=null)
                {
                    profilDto.setType("morale");
                }
            }
        }
        return ResponseEntity.ok(profilDto);
    }

    @GetMapping("getinfouser/{iduser}")
    public ResponseEntity<?> getInfoUser(@PathVariable Integer iduser) throws Exception {
        User user=userService.getUnique(Long.valueOf((iduser)));
        return ResponseEntity.ok(user);
    }

    @PatchMapping("setactif")
    public ResponseEntity<?>setEnable(@RequestBody UserDto userDto) throws Exception {
        User user=userService.getUnique((long) Math.toIntExact((userDto.getId())));
        user.setActive(userDto.isActive());
        userRepository.save(user);
        if(user.getTraducteur()!=null)
        {
            Traducteur traducteur=user.getTraducteur();
            traducteur.setActive(userDto.isActive());
            traducteurRepository.save(traducteur);
        }
        return ResponseEntity.ok("Opération effectué avec succès");
    }

    @PatchMapping("setpresent")
    public ResponseEntity<?>setPresent(@RequestBody UserDto userDto) throws Exception {
        User user=userService.getUnique((long) Math.toIntExact((userDto.getId())));
        user.setPresent(userDto.isPresent());
        userRepository.save(user);
        if(user.getTraducteur()!=null)
        {
            Traducteur traducteur=user.getTraducteur();
            traducteur.setPresent(userDto.isPresent());
            traducteurRepository.save(traducteur);
        }
        return ResponseEntity.ok("Opération effectué avec succès");
    }
}
