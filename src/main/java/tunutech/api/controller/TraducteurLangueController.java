package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.TraducteurLangueDto;
import tunutech.api.model.Langue;
import tunutech.api.model.TraducteurLangue;
import tunutech.api.services.TraducteurLangueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/traductorLangage/")
public class TraducteurLangueController {
    @Autowired
    private TraducteurLangueService traducteurLangueService;

    @GetMapping("alloftraducteur/{idtraducteur}")
    public ResponseEntity<?>getofTraducteur(@PathVariable Long id)
    {
        return ResponseEntity.ok(traducteurLangueService.getOfTraducteur(id));
    }

    @GetMapping("alloftraducteurAsLanguages/{id}")
    public ResponseEntity<?>getofTraducteurasLanguages(@PathVariable Long id)
    {
        return ResponseEntity.ok(traducteurLangueService.getOfTraducteursLanguages(id));
    }

    @PutMapping("setlangues/{id}/langues")
    public ResponseEntity<?> updateLanguesTraducteur(
            @PathVariable Long id,
            @RequestBody Map<String, List<Langue>> request) {

        try {
            List<Langue> nouvellesLangues = request.get("langues");

            // Validation basique
            if (nouvellesLangues == null) {
                return ResponseEntity.badRequest().body("Le champ 'langues' est requis");
            }

            // Mettre à jour les langues du traducteur
            traducteurLangueService.setLangues(id, nouvellesLangues);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Langues mises à jour avec succès",
                    "nombreLangues", nouvellesLangues.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour des langues: " + e.getMessage());
        }
    }

    @PostMapping("add")
    public ResponseEntity<?>createTraducteurLangue(@RequestBody TraducteurLangueDto traducteurLangueDto)
    {
        try{
            if(traducteurLangueService.ifExist(traducteurLangueDto.getIdtraducteur(),traducteurLangueDto.getIdlangue()))
            {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Ce enregistrement existe déjà");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            return  ResponseEntity.ok(traducteurLangueService.createTraducteurLangue(traducteurLangueDto));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            traducteurLangueService.delete(id);
            return ResponseEntity.ok("Enregistrement supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Enregistrement non trouvé");
        }
    }
}
