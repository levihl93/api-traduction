package tunutech.api.services.implementsServices;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.ActivityDTO;
import tunutech.api.dtos.ProjectDto;
import tunutech.api.dtos.ProjectResponseDto;
import tunutech.api.dtos.TranslationProjectsDto;
import tunutech.api.exception.CalculMontantException;
import tunutech.api.exception.TarifNotFoundException;
import tunutech.api.model.*;
import tunutech.api.repositories.*;
import tunutech.api.services.*;
import tunutech.api.Utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectImpl implements ProjetService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private ProjetTraducteurRepository projetTraducteurRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ProjetLangueCibleService projetLangueCibleService;

    @Autowired
    private ProjetLangueSourceService projetLangueSourceService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    ComplexityRepository complexityRepository;

    @Autowired
    private Functions functions;
    @Override
    public List<Project> listall() {
        return projectRepository.findAll();
    }

    @Override
    public Double calculerMontantAutomatique(Project projet,String projectComplexity,String documentType) {
        try {
            Tarif tarif = priceRepository.findByTypeDocument(TypeDocument.valueOf(documentType));

            if(tarif == null) {
                throw new TarifNotFoundException("Aucun Tarif fixé pour le type de document: " + documentType);
            }

            System.out.println("non nul");
            double tarifBase = tarif.getMotPrice();
            Double multiplicateurComplexite = 1.0;

            if(!projectComplexity.isEmpty()) {
                Complexity complexity = complexityRepository.findByProjectComplexity(ProjectComplexity.valueOf(projectComplexity));
                if(complexity != null) {
                    multiplicateurComplexite = complexity.getMultiplicationCompexity();
                }
            }

            return projet.getWordscount() * tarifBase * multiplicateurComplexite;
        }catch (IllegalArgumentException e) {
            throw new CalculMontantException("Type de document ou complexité invalide: " + e.getMessage());
        }

    }

    @Override
    public Long NumberofProject() {
        return projectRepository.count();
    }

    @Override
    public List<Project> listvalider(Boolean valider) {
        return projectRepository.findByValider(valider);
    }

    @Override
    public String getLanguesSources(Project project) {
            String languesosurces="";
            Integer intr=0;
            List<Langue> langueSources=new ArrayList<Langue>();
            langueSources=projetLangueSourceService.Listofproject(project.getId());
            Integer nbsources=langueSources.size();
            for(Langue langue:langueSources)
            {
                languesosurces+=langue.getName();
                intr++;
                if(intr<nbsources)
                {
                    languesosurces+=";";
                }
            }
            return languesosurces;
    }

    @Override
    public String getLanguesCibles(Project project) {
        String languesosurces="";
        Integer intr=0;
        List<Langue> langueCibles=new ArrayList<Langue>();
        langueCibles=projetLangueCibleService.Listofproject(project.getId());
        Integer nbsources=langueCibles.size();
        for(Langue langue:langueCibles)
        {
            languesosurces+=langue.getName();
            intr++;
            if(intr<nbsources)
            {
                languesosurces+=";";
            }
        }

        return languesosurces;
    }

    @Override
    public List<Project> ListofClient(Long idclient) {
        return projectRepository.findByClientId(idclient);
    }

    @Override
    public List<Project> ListofTraducteur(Long idtranslator) {
        List<Project> resultats=new ArrayList<>();
        for(ProjetTraducteur projetTraducteur:projetTraducteurRepository.findByTraducteurId(idtranslator))
        {
            resultats.add(projetTraducteur.getProject());
        }
        return resultats;
    }


    @Override
    public List<Project> Listterminer(Boolean terminer) {
        return projectRepository.findByTerminer(terminer);
    }

    @Override
    public List<Project> ListterminerofClient(Long idclient) {
        return null;
    }

    @Override
    public Project saveproject(ProjectDto projectDto) {
        Project project=new Project();
        Client client=clientService.getUnique(projectDto.getIdclient());
        project.setCode(projectDto.getCode());
        project.setDescription(projectDto.getDescription());
        project.setTitle(projectDto.getTitle());
        project.setPriorityType(projectDto.getPriorityType());
        project.setPriceperWord(projectDto.getPriceperWord());
        project.setEstimatedPrice(projectDto.getEstimatedPrice());
        project.setWordscount(projectDto.getWordscount());
        project.setTypeDocument(projectDto.getTypeDocument());
        project.setDatevoulue(projectDto.getDatevoulue());
        project.setProjectStatus(ProjectStatus.PENDING);
        project.setClient(client);
        Project savedProject=projectRepository.save(project);
        Optional<User> user=userRepository.findByClientId(client.getId());
        if(user.isPresent())
        {
            try {
                activityService.logProjectActivity(user.get(),ActivityType.PROJECT_CREATED,project.getTitle(),user.get().getFullName(), project.getId(), project.getDescription());
            }catch (Exception e)
            {
                throw  new RuntimeException(e.getMessage());
            }
        }
        return  savedProject;
    }

    @Override
    public Project getUniquebyId(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    @Override
    public Project getUniquebyCode(String code) {
        return projectRepository.findByCode(code);
    }

    @Override
    public String generateCode(Long idclient) {
        String code;
        do
        {
            code=functions.generateCodeUnique(10, Math.toIntExact(idclient));
        }
        while (projectRepository.existsByCode(code));
        code="PJ-"+idclient+"-"+code;
        return code;
    }

    @Override
    public ProjectResponseDto mapProject(Project project) {
        Float nbmots= 0.00F;
       Optional<Traducteur> traducteur=this.getTraducteorofProject(project);
       Optional<Contrat> contrat=contratRepository.findByProjectId(project.getId());
       ProjectResponseDto projectResponseDto=new ProjectResponseDto();
            List<Document> documentlist=documentService.ListbyIdProject(project.getId());
            for(Document document:documentlist)
            {
                nbmots+=document.getWordsCount();
            }
            projectResponseDto.setCode(project.getCode());
            projectResponseDto.setDescription(project.getDescription());
            projectResponseDto.setTitle(project.getTitle());
            projectResponseDto.setPriority(project.getPriorityType());
            projectResponseDto.setClient(project.getClient());
            projectResponseDto.setIdentiteclient(project.getClient().getFullName());
            projectResponseDto.setLanguesources(projetLangueSourceService.Listofproject(project.getId()));
            projectResponseDto.setLanguetarget(projetLangueCibleService.Listofproject(project.getId()));
            projectResponseDto.setId(project.getId());
            projectResponseDto.setStartDate(project.getCreatedAt());
            projectResponseDto.setDeadline(project.getDatevoulue());
            projectResponseDto.setProgress(20.00);
            projectResponseDto.setTypeDocument(project.getTypeDocument());
            projectResponseDto.setProjectStatus(project.getProjectStatus());
            projectResponseDto.setWordsCount(nbmots);
            projectResponseDto.setEstimatedPrice(project.getEstimatedPrice());
            projectResponseDto.setPricePerWord(project.getPriceperWord());
            projectResponseDto.setAnnuler(project.getAnnuler());
            projectResponseDto.setTerminer(project.getTerminer());
            projectResponseDto.setDocumentlist(documentlist);

            if(traducteur!=null && traducteur.isPresent())
            {
                projectResponseDto.setTranslator(traducteur);
                Optional<ProjetTraducteur> projetTraducteur=projetTraducteurRepository.findByTraducteurIdAndProjectId(traducteur.get().getId(), project.getId());
                if(projetTraducteur.isPresent())
                {
                    projectResponseDto.setAssignedDate(projetTraducteur.get().getCreatedAt());
                }
            }

            if(contrat!=null && contrat.isPresent())
            {
                    projectResponseDto.setBudget(functions.formatMontant(contrat.get().getMontantContrat(),Devise.USD));
            }
            String languesosurces="";
            Integer intr=0;
            Integer nbsources=projectResponseDto.getLanguesources().size();
            for(Langue langue:projectResponseDto.getLanguesources())
            {
                languesosurces+=langue.getName();
                intr++;
                if(intr<nbsources)
                {
                    languesosurces+=";";
                }
            }
            projectResponseDto.setSourceslangues(languesosurces);
            String languestarget="";
            Integer intrr=0;
            Integer nbsourcesr=projectResponseDto.getLanguetarget().size();

            for(Langue langue:projectResponseDto.getLanguetarget())
            {
                languestarget+=langue.getName();
                intrr++;
                if(intr<nbsourcesr)
                {
                    languestarget+=";";
                }
            }
            projectResponseDto.setTargetlangues(languestarget);
            List<ActivityDTO> activityList=activityService.getRecentActivitiesOfProject(1, project.getId());
            if(!activityList.isEmpty())
            {
                for(ActivityDTO activityDTO:activityList)
                {
                    projectResponseDto.setLastActivity(activityDTO.getCreatedAt());
                }
            }
            return  projectResponseDto;
    }

    @Override
    public TranslationProjectsDto bigMap(List<ProjectResponseDto> list) {
        List<ProjectResponseDto> pending=new ArrayList<>();
        List<ProjectResponseDto> current=new ArrayList<>();
        List<ProjectResponseDto> completed=new ArrayList<>();
        for(ProjectResponseDto projectResponseDto:list)
        {
            switch (projectResponseDto.getProjectStatus())
            {
                case PENDING: pending.add(projectResponseDto);
                break;
                case IN_PROGRESS: current.add(projectResponseDto);
                break;
                case COMPLETED: completed.add(projectResponseDto);
                break;
            }
        }
        return new TranslationProjectsDto(pending,current,completed);
    }


    @Override
    public Project update(ProjectDto projectDto) {
        Project project=projectRepository.findByCode(projectDto.getCode());
        project.setDescription(projectDto.getDescription());
        project.setTitle(projectDto.getTitle());
        project.setPriorityType(projectDto.getPriorityType());
        project.setPriceperWord(projectDto.getPriceperWord());
        project.setEstimatedPrice(projectDto.getEstimatedPrice());
        project.setWordscount(projectDto.getWordscount());
        project.setTypeDocument(projectDto.getTypeDocument());
        project.setDatevoulue(projectDto.getDatevoulue());
        Project savedProject=projectRepository.save(project);
        Optional<User> user=userRepository.findByClientId(project.getClient().getId());
        if(user.isPresent())
        {
            try {
                activityService.logProjectActivity(user.get(),ActivityType.PROJECT_UPDATED,project.getTitle(),user.get().getFullName(), project.getId(), project.getDescription());
            }catch (Exception e)
            {
                throw  new RuntimeException(e.getMessage());
            }
        }
        return  savedProject;
    }

    @Override
    public Optional<Traducteur> getTraducteorofProject(Project project) {
        Optional<ProjetTraducteur> projetTraducteur=projetTraducteurRepository.findByProjectId(project.getId());
        Traducteur traducteur=new Traducteur();
        if(projetTraducteur.isPresent())
        {
          return Optional.ofNullable(projetTraducteur.get().getTraducteur());
        }else {
            return  null;
        }
    }

}
