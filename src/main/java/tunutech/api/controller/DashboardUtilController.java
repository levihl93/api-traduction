package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tunutech.api.dtos.StatUsersResponse;
import tunutech.api.services.ClientService;
import tunutech.api.services.ProjetService;
import tunutech.api.services.TraducteurService;

@RestController
@RequestMapping("/dashboardutil/")
public class DashboardUtilController {
    @Autowired
    ClientService clientService;
    @Autowired
    TraducteurService traducteurService;
    @Autowired
    ProjetService projetService;

    @GetMapping("statusers")
    public ResponseEntity<?>getStatUsers()
    {
        Long countclient = clientService.Number0fClients();
        Integer counttraducteur = traducteurService.numberTraducteurPresent(true);
        Long countproject=projetService.NumberofProject();

        StatUsersResponse statUsersResponse=new StatUsersResponse();
        statUsersResponse.setNumberClient(countclient);
        statUsersResponse.setNumberTraducteur(Long.valueOf(counttraducteur));
        statUsersResponse.setNumberProject(countproject);
        return ResponseEntity.ok(statUsersResponse);
    }
}
