package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tunutech.api.Utils.Functions;
import tunutech.api.dtos.ContratDTO;
import tunutech.api.model.*;
import tunutech.api.repositories.ContratRepository;
import tunutech.api.repositories.ProjectRepository;
import tunutech.api.services.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContratServiceImpl implements ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ProjetService projetService;

    @Autowired
    private SettingAppService settingAppService;

    @Autowired
    private Functions functions;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Override
    public Contrat createContrat(Long projetId, User user,String complexity,String type,Integer nbjours) {
        Project projet = projectRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Contrat contrat = new Contrat();
        contrat.setCode(generateCode(projet));
        contrat.setNombreJours(nbjours);
        contrat.setUser(user);
        contrat.setProjectComplexity(ProjectComplexity.valueOf(complexity));
        projet.setTypeDocument(TypeDocument.valueOf(type));
        projet.setProjectStatus(ProjectStatus.ANALYSIS);
        projectRepository.save(projet);


        // Détails techniques du projet
        remplirDetailsTechniques(contrat, projet);
        // Calculs automatiques
        effectuerCalculsAutomatiques(contrat, projet);
        // Génération du contenu
        genererContenuContrat(contrat,projet);
        Contrat contratcreated=contratRepository.save(contrat);
        try {
            activityService.logProjectActivity(user,ActivityType.CONTRACT_GENERATED,projet.getTitle(),projet.getClient().getFullName(), projet.getId(), projet.getDescription());
        }catch (Exception e)
        {
            throw  new RuntimeException(e.getMessage());
        }
        return  contratcreated;
    }

    @Override
    public Contrat getById(Long contractId) {
        return contratRepository.findById(contractId)
                .orElseThrow(()->new RuntimeException("Contract not found"));
    }

    @Override
    public Boolean ifExistforProject(Long projectId) {
        Boolean res=false;
        Optional <Contrat> contrat=contratRepository.findByProjectId(projectId);
        if(contrat.isPresent())
        {
            res=true;
        }
        return res;
    }

    @Override
    public Contrat getofProject(String code) {
        Project project=projectRepository.findByCode(code);
        if(project.getId()!=null || project.getId()!=0)
        {
            Optional<Contrat> contrat=contratRepository.findByProjectId(project.getId());
            if(contrat.isPresent())
            {
                return contrat.get();
            }throw  new RuntimeException("Contract not found");
        }throw new RuntimeException("Project not found");
    }

    @Override
    public Contrat getofProjectbyProjectId(Long projectId) {

            Optional<Contrat> contrat=contratRepository.findByProjectId(projectId);
            if(contrat.isPresent())
            {
                return contrat.get();
            }throw  new RuntimeException("Contract not found");
    }

    @Override
    @Transactional
    public void deleteByProjectId(long projectId) {
        contratRepository.deleteByProjectId(projectId);
    }

    @Override
    public String generateCode(Project project) {
        String code;
        do
        {
            code=functions.generateCodeUnique(10, Math.toIntExact(project.getId()));
        }
        while (projectRepository.existsByCode(code));
        code="CT-"+project.getId()+"-"+code;
        return code;
    }

    @Override
    public Contrat update(ContratDTO contratDTO) {
        Contrat contrat=contratRepository.findById(contratDTO.getId()).orElseThrow(()->new RuntimeException("Contract not found"));
       contrat.setContratStatut(contratDTO.getContratStatut());
       contrat.setApproved_At(LocalDateTime.now());
       User user= userService.getByClient(contrat.getProject().getClient().getId());
        try {
            activityService.logProjectActivity(user,ActivityType.CONTRACT_ACCEPTED,contrat.getProject().getTitle(),contrat.getProject().getClient().getFullName(), contrat.getProject().getId(), contrat.getProject().getDescription());
        }catch (Exception e)
        {
            throw  new RuntimeException(e.getMessage());
        }
        return contratRepository.save(contrat);
    }

    @Override
    public void effectuerCalculsAutomatiques(Contrat contrat, Project project) {
        // Calcul du montant proposé
        Double montant = calculerMontantAutomatique(project,String.valueOf(contrat.getProjectComplexity()),String.valueOf(project.getTypeDocument()));
        Double majoration =0.0;
        // Calcul de la majoration
        if(contrat.getMajorationPourcentage()>0)
        {
             majoration = montant*(contrat.getMajorationPourcentage() / 100);
        }
        contrat.setMontantContrat(montant+majoration);
        contrat.setMontatMajoration(majoration);

        // Calcul du délai
        Integer delaiJours = contrat.getNombreJours();
       // contrat.setNombreJours(delaiJours);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, delaiJours);
        contrat.setEcheanceContrat(cal.getTime());
    }


    private void remplirDetailsTechniques(Contrat contrat, Project projet) {
        contrat.setProject(projet);
        contrat.setClientName(projet.getClient().getFullName());
        contrat.setClientAdresse(projet.getClient().getAdresse());
        contrat.setClientEmail(projet.getClient().getEmail());
        contrat.setClientPays(projet.getClient().getPays());
        contrat.setNombreMots(projet.getWordscount());
        contrat.setContratStatut(ContratStatut.EN_ATTENTE);
        contrat.setDevise(Devise.USD);
        SettingsApp settingsApp=settingAppService.getSettingsApp();
        contrat.setMajorationPourcentage(0.0);
        if(settingsApp!=null)
        {
            contrat.setMajorationPourcentage(settingAppService.getSettingsApp().getMajoration());
        }

    }

    private Double calculerMontantAutomatique(Project projet, String complexity, String type) {
        return projetService.calculerMontantAutomatique(projet,complexity,type);
    }

    private Integer estimerDelai(Project projet) {
        // Estimation basée sur le nombre de mots et complexité
        int motsParJour = getMotsParJour(projet.getTypeDocument());
        int delaiBase = (int) Math.ceil((double) projet.getWordscount() / motsParJour);

        // Ajouter une marge
        return delaiBase + 2;
    }

    private int getMotsParJour(TypeDocument typeDocument) {
        Map<String, Integer> productivite = Map.of(
                "GENERAL", 2500,
                "TECHNIQUE", 1500,
                "JURIDIQUE", 1200,
                "MEDICAL", 1000
        );
        return productivite.getOrDefault(typeDocument, 2000);
    }

    private void genererContenuContrat(Contrat contrat, Project project) {
        String conditions = """
            CONDITIONS GÉNÉRALES DE TRADUCTION
            
            1. PRESTATIONS : Le traducteur s'engage à fournir une traduction de qualité professionnelle 
            du document de %s vers %s.
            
            2. DÉLAI : La traduction sera livrée au plus tard le %s.
            
            3. PAIEMENT : Un acompte de %s%% (%s %s) est exigible à la signature, 
            le solde à la livraison.
            
            4. CONFIDENTIALITÉ : Le traducteur s'engage à la confidentialité totale des documents.
            
            5. RÉVISION : Une révision mineure est incluse dans le prix.
            """.formatted(
                projetService.getLanguesSources(project),
                projetService.getLanguesCibles(project),
                new SimpleDateFormat("dd/MM/yyyy").format(contrat.getEcheanceContrat()),
                contrat.getMajorationPourcentage(),
                contrat.getMontantContrat(),
                contrat.getDevise()
        );

        contrat.setConditionsGenerales(conditions);

        contrat.setConditionsSpeciales("RAS");
    }
}
