package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.ClientDto;
import tunutech.api.dtos.RegisterUserDto;
import tunutech.api.dtos.TraducteurDto;
import tunutech.api.model.Client;
import tunutech.api.model.Traducteur;
import tunutech.api.model.User;
import tunutech.api.model.UserEnableToken;
import tunutech.api.repositories.ClientRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.AuthenticationService;
import tunutech.api.services.ClientService;
import tunutech.api.services.UserEnableService;
import tunutech.api.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer/")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("all")
    public List<Client> getAllClient()
    {
        return clientService.allclient();
    }
    @GetMapping("allpresent")
    public List<Client> getAllClientPresent()
    {
        return clientService.allclientPresent(true);
    }
    @GetMapping("getUnique/{idclient}")
    public ResponseEntity<?> getInfo(@PathVariable Long idclient) throws Exception {
        Client client=clientService.getUnique(idclient);
        return ResponseEntity.ok(client);
    }

    @GetMapping("number")
    public ResponseEntity<Long> getNumberofClients()
    {
        Long count = clientService.Number0fClients();
        return ResponseEntity.ok(count);
    }

    @PutMapping("edit")
    public ResponseEntity<?>updateClient(@RequestBody ClientDto clientDto)
    {
        return ResponseEntity.ok(clientService.updateClient(clientDto));
    }
    @PatchMapping("setpresent")
    public ResponseEntity<?>setPresent(@RequestBody ClientDto clientDto) throws Exception {
        Client client=clientService.getUnique(clientDto.getClientid());
        client.setPresent(clientDto.getPresent());
        clientRepository.save(client);
        User user=userService.getByEmailByForce(client.getEmail());
        user.setPresent(clientDto.getPresent());
        userRepository.save(user);
        //return ResponseEntity.ok(user.getEmail());
        return ResponseEntity.ok("Opération effectué avec succès");
    }
    @PatchMapping("setactif")
    public ResponseEntity<?>setActif(@RequestBody ClientDto clientDto) throws Exception {
        Client client=clientService.getUnique(clientDto.getClientid());
        client.setActive(clientDto.getActive());
        clientRepository.save(client);
        User user=userService.getByEmailByForce(client.getEmail());
        user.setActive(clientDto.getActive());
        userRepository.save(user);
        //return ResponseEntity.ok(user.getEmail());
        return ResponseEntity.ok("Opération effectué avec succès");
    }
}
