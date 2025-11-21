package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.ContratDTO;
import tunutech.api.dtos.ContratResponseDTO;
import tunutech.api.model.*;
import tunutech.api.services.ContratDFormattingService;
import tunutech.api.services.ContratService;
import tunutech.api.services.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats")
public class ContratController {

    @Autowired
    private ContratService contratGenerationService;

    @Autowired
    private ContratDFormattingService contratDFormattingService;

    @Autowired
    private UserService userService;


    @PostMapping("/generer")
    public ResponseEntity<?> genererContrat(@RequestBody ContratDTO contratDTO) {
        Optional <User> user=userService.getById(contratDTO.getUserId());
        if(user.isPresent())
        {
            try {
                if(user.get().getRoleUser()!= RoleUser.ADMIN) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Vous n'êtes pas autorisé à exécuter cette action");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
                Contrat contrat = contratGenerationService.createContrat(contratDTO.getProjectId(),user.get(),contratDTO.getComplexity(),contratDTO.getType(),contratDTO.getNbjours());
                ContratResponseDTO contratResponseDTO=contratDFormattingService.formaterContratPourAffichage(contrat);
                return ResponseEntity.ok(contratResponseDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }throw new RuntimeException("User not found");

    }

    @GetMapping("/projet/{projectCode}")
    public ResponseEntity<?> getContrat(@PathVariable String projectCode) {
        try {
            Contrat contrat = contratGenerationService.getofProject(projectCode);
            ContratResponseDTO contratResponseDTO=contratDFormattingService.formaterContratPourAffichage(contrat);
            return ResponseEntity.ok(contratResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ifExist/{projectId}")
    public ResponseEntity<?> ifexistForProject(@PathVariable Long projectId) {
        try {
            Contrat contrat=new Contrat();
            ContratExistResponseDTO contratExistResponseDTO=new ContratExistResponseDTO();
            contratExistResponseDTO.setContratexiste(contratGenerationService.ifExistforProject(projectId));
            if(contratExistResponseDTO.getContratexiste())
            {
                contrat=contratGenerationService.getofProjectbyProjectId(projectId);
            }
           contratExistResponseDTO.setContrat(contrat);
            return ResponseEntity.ok(contratExistResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/setAccepted/{contractId}")
    @Transactional
    public ResponseEntity<?> setAccepted(@PathVariable Long contractId) {
        try {
            ContratDTO contratDTO=new ContratDTO();
            contratDTO.setId(contractId);
            contratDTO.setContratStatut(ContratStatut.ACCEPTE);
            return ResponseEntity.ok(contratGenerationService.update(contratDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
