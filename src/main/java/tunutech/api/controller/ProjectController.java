package tunutech.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tunutech.api.dtos.*;
import tunutech.api.exception.DocumentStorageException;
import tunutech.api.exception.DocumentValidationException;
import tunutech.api.model.*;
import tunutech.api.repositories.ProjectRepository;
import tunutech.api.services.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project/")
@CrossOrigin(origins = "*")
@Slf4j
public class ProjectController {

    @Autowired
    private ProjetService projetService;

    @Autowired
    private LangueService langueService;

    @Autowired
    private ProjetLangueSourceService projetLangueSourceService;

    @Autowired
    private ProjetLangueCibleService projetLangueCibleService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ContratService contratService;

    @PostMapping(value = "add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idclient") Long idclient,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("wordscount") Float wordscount,
            @RequestParam("estimatedPrice") Float estimatedPrice,
            @RequestParam("priceperWord") Float priceperWord,
            @RequestParam("priorityType") String priorityType,
            @RequestParam("documentType") String documentType,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("sourceLanguages") List<String> languessources,
            @RequestParam("ciblesLanguages") List<String> languescibles,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        try {
            ProjectDto projectDto=new ProjectDto();
            projectDto.setIdclient(idclient);
            projectDto.setDescription(description);
            projectDto.setTitle(title);
            projectDto.setDatevoulue(date);
            projectDto.setPriceperWord(priceperWord);
            projectDto.setEstimatedPrice(estimatedPrice);
            projectDto.setTypeDocument(TypeDocument.valueOf(documentType));
            projectDto.setPriorityType(PriorityType.valueOf(priorityType));
            projectDto.setWordscount(wordscount);
            String codeproject= projetService.generateCode(idclient);
            projectDto.setCode(codeproject);
            Project project=projetService.saveproject(projectDto);
            // Vérifier que le projet n'est pas null.03333333333333333.
            if (project == null) {
                throw new RuntimeException("Erreur lors de la création du projet");
            }

            for(String codeangue:languessources)
            {
                Langue langue=langueService.getUniquebyCode(codeangue);
                if (langue != null) {
                    ProjetLangueSourceDto projetLangueSourceDto = new ProjetLangueSourceDto();
                    projetLangueSourceDto.setIdlangue(langue.getId());
                    projetLangueSourceDto.setProject(project);
                    projetLangueSourceService.add(projetLangueSourceDto);
                }
            }
            for(String codeangue:languescibles)
            {
                Langue langue=langueService.getUniquebyCode(codeangue);
                ProjetLangueCibleDto projetLangueCibleDto=new ProjetLangueCibleDto();
                projetLangueCibleDto.setIdlangue(langue.getId());
                projetLangueCibleDto.setProject(project);
                projetLangueCibleService.add(projetLangueCibleDto);
            }

            Document document = documentService.storeDocument(file, project,category,wordscount);

            DocumentResponse response = DocumentResponse.builder()
                    .id(document.getId())
                    .originalName(document.getOriginalName())
                    .storedName(document.getStoredName())
                    .fileSize(document.getFileSize())
                    .contentType(document.getContentType())
                    .codeProject(document.getProject().getCode())
                    .status(document.getStatus())
                    .uploadDate(document.getUploadDate())
                    .message("Document uploadé avec succès")
                    .build();

            return ResponseEntity.ok(response);

        } catch (DocumentValidationException e) {
            log.warn("Validation échouée: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    DocumentResponse.error("Erreur validation: " + e.getMessage()));

        } catch (DocumentStorageException e) {
            log.error("Erreur stockage: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    DocumentResponse.error("Erreur stockage: " + e.getMessage()));
        }
    }

    @PutMapping(value = "update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upldateProject(
            @RequestParam("file") MultipartFile file,
            @RequestParam("idclient") Long idclient,
            @RequestParam("title") String title,
            @RequestParam("code") String code,
            @RequestParam("description") String description,
            @RequestParam("wordscount") Float wordscount,
            @RequestParam("estimatedPrice") Float estimatedPrice,
            @RequestParam("pricePerWord") Float priceperWord,
            @RequestParam("priorityType") String priorityType,
            @RequestParam("documentType") String documentType,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("sourceLanguages") List<String> languessources,
            @RequestParam("ciblesLanguages") List<String> languescibles,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        try {
            ProjectDto projectDto=new ProjectDto();
            projectDto.setIdclient(idclient);
            projectDto.setDescription(description);
            projectDto.setTitle(title);
            projectDto.setDatevoulue(date);
            projectDto.setPriceperWord(priceperWord);
            projectDto.setEstimatedPrice(estimatedPrice);
            projectDto.setTypeDocument(TypeDocument.valueOf(documentType));
            projectDto.setPriorityType(PriorityType.valueOf(priorityType));
            projectDto.setWordscount(wordscount);
            projectDto.setCode(code);
            Project project=projetService.update(projectDto);
            // Vérifier que le projet n'est pas null.03333333333333333.
            if (project == null) {
                throw new RuntimeException("Erreur lors de la création du projet");
            }


            //Supprimer leventuel contrat du projet
            if(contratService.ifExistforProject(project.getId()))
            {
                contratService.deleteByProjectId(project.getId());
            }
            ///supprimer les aciennes langues enregistre pour le project

            projetLangueCibleService.deleteallofProject(project);
            projetLangueSourceService.deleteAllOfProject(project);

            for(String codeangue:languessources)
            {
                Langue langue=langueService.getUniquebyCode(codeangue);
                if (langue != null) {
                    ProjetLangueSourceDto projetLangueSourceDto = new ProjetLangueSourceDto();
                    projetLangueSourceDto.setIdlangue(langue.getId());
                    projetLangueSourceDto.setProject(project);
                    projetLangueSourceService.add(projetLangueSourceDto);
                }
            }
            for(String codeangue:languescibles)
            {
                Langue langue=langueService.getUniquebyCode(codeangue);
                ProjetLangueCibleDto projetLangueCibleDto=new ProjetLangueCibleDto();
                projetLangueCibleDto.setIdlangue(langue.getId());
                projetLangueCibleDto.setProject(project);
                projetLangueCibleService.add(projetLangueCibleDto);
            }

            Document document=new Document();
            ///supprimer lancien doc dans la BDD
                System.out.println(file.getOriginalFilename());

            if(!(file.getOriginalFilename()==""))
            {
                documentService.deleteDocOnBdd(project);
                document = documentService.storeDocument(file, project,category,wordscount);
            }else {
                    document=documentService.getUniquebyIdProject(project.getId());
            }

            DocumentResponse response = DocumentResponse.builder()
                    .id(document.getId())
                    .originalName(document.getOriginalName())
                    .storedName(document.getStoredName())
                    .fileSize(document.getFileSize())
                    .contentType(document.getContentType())
                    .codeProject(document.getProject().getCode())
                    .status(document.getStatus())
                    .uploadDate(document.getUploadDate())
                    .message("Document uploadé avec succès")
                    .build();

            return ResponseEntity.ok(response);

        } catch (DocumentValidationException e) {
            log.warn("Validation échouée: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    DocumentResponse.error("Erreur validation: " + e.getMessage()));

        } catch (DocumentStorageException e) {
            log.error("Erreur stockage: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(
                    DocumentResponse.error("Erreur stockage: " + e.getMessage()));
        }
    }

    @GetMapping("getunique/{idproject}")
    public ResponseEntity<?>getUniquebyId(@PathVariable Long idproject)
    {
        Project project=projetService.getUniquebyId(idproject);
            return  ResponseEntity.ok(projetService.mapProject(project));
    }

    @GetMapping("getuniquebyCode/{code}")
    public ResponseEntity<?>getUniquebyCode(@PathVariable String code)
    {
        Project project=projetService.getUniquebyCode(code);
            return  ResponseEntity.ok(projetService.mapProject(project));
    }

    @GetMapping("listofClientprofil/{idclient}")
    public ResponseEntity<?>getlistofClientprofil(@PathVariable Long idclient)
    {
        List<ProjectResponseDto> listprojectresponse = projetService.ListofClient(idclient)
                .stream()
                .map(projetService::mapProject)
                .collect(Collectors.toList());

        return ResponseEntity.ok(projetService.bigMap(listprojectresponse));
    }

    @GetMapping("listofClient/{idclient}")
    public ResponseEntity<?>getlistofClient(@PathVariable Long idclient)
    {
        List<ProjectResponseDto> listprojectresponse = projetService.ListofClient(idclient)
                .stream()
                .map(projetService::mapProject)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listprojectresponse);
    }

    @GetMapping("listall")
    public ResponseEntity<?>getlist()
    {
        List<ProjectResponseDto> listprojectresponse = projetService.listall()
                .stream()
                .map(projetService::mapProject)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listprojectresponse);
    }

    @GetMapping("listoftraducteur")
    public ResponseEntity<?>getlistofTraducteur(@AuthenticationPrincipal User user)
    {
        List<ProjectResponseDto> listprojectresponse = projetService.ListofTraducteur(user.getTraducteur().getId())
                .stream()
                .map(projetService::mapProject)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listprojectresponse);
    }
}
