package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.LangueDto;
import tunutech.api.model.Langue;
import tunutech.api.repositories.LangueRepository;
import tunutech.api.services.LangueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/language/")
public class LangueController {
    @Autowired
    private LangueService langueService;
    @Autowired
    private LangueRepository langueRepository;

    @GetMapping("all")
    public List<Langue> getAll()
    {
        return langueService.listall();
    }

    @GetMapping("allpresent")
    public List<Langue> getAllPresent()
    {
        return langueService.listallpresent(true);
    }

    @PostMapping("add")
    public ResponseEntity<?>createLanguage(@RequestBody LangueDto langueDto)
    {
        try{
                if(langueService.ifNameExist(langueDto.getName()))
                {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "La langue existe déjà");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
                if(langueService.ifCodeExist(langueDto.getCode()))
                {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Le code existe déjà");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
                return ResponseEntity.ok(langueService.saveLangue(langueDto));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("edit")
    public ResponseEntity<?>updateLanguage(@RequestBody LangueDto langueDto)
    {
        try{
                if(!langueService.ifExist(langueDto.getId()))
                {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "La langue n'existe pas");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
            if(langueService.ifNameExist(langueDto.getName()))
            {
               Langue langue= langueService.getUniquebyName(langueDto.getName());
                if(langue.getId()!=langueDto.getId())
                {
                    System.out.println(("trouve"));
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Une autre langue possède  déjà ce nom");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
            }
            if(langueService.ifCodeExist(langueDto.getCode())) {
                Langue langue= langueService.getUniquebyCode(langueDto.getCode());
                if(langue.getId()!=langueDto.getId())
                {
                Map<String, String> error = new HashMap<>();
                    error.put("error", "Une autre langue possède  déjà ce code");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
            }
                return ResponseEntity.ok(langueService.updateLangue(langueDto));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("setstatut")
    public ResponseEntity<?>setStatut(@RequestBody LangueDto langueDto)
    {
        try{
                if(!langueService.ifExist(langueDto.getId()))
                {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "La langue n'existe pas");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
                return ResponseEntity.ok(langueService.setEnableLangue(langueDto));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("setpresent")
    public ResponseEntity<?>setPresent(@RequestBody LangueDto langueDto)
    {
        try{
                if(!langueService.ifExist(langueDto.getId()))
                {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "La langue n'existe pas");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
                }
                return ResponseEntity.ok(langueService.setPresentLangue(langueDto));
        }catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
