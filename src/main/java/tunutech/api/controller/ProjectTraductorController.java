package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tunutech.api.dtos.ProjetTraducteurDto;
import tunutech.api.services.ProjetTraducteurService;

@RestController
@RequestMapping("/projecttraductor/")
public class ProjectTraductorController {
    @Autowired
    private ProjetTraducteurService projetTraducteurService;

    @PostMapping("add")
    public ResponseEntity<?>addTraductoratProject(@RequestBody ProjetTraducteurDto projetTraducteurDto)
    {
        return  ResponseEntity.ok(projetTraducteurService.create(projetTraducteurDto));
    }
}
