package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.ProjetTraducteurDto;
import tunutech.api.model.*;
import tunutech.api.repositories.ProjectRepository;
import tunutech.api.repositories.ProjetTraducteurRepository;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetTraducteurServiceImpl implements ProjetTraducteurService {
    @Autowired
    ProjetTraducteurRepository projetTraducteurRepository;
    @Autowired
    private TraducteurRepository traducteurRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActivityService activityService;
    @Override
    public ProjetTraducteur create(ProjetTraducteurDto projetTraducteurDto) {
        Optional<Traducteur> traducteur=traducteurRepository.findById(projetTraducteurDto.getTraducteurId());
        Optional<Project> projet=projectRepository.findById(projetTraducteurDto.getProjectId());
        if(!projet.isPresent())
        {
            throw new RuntimeException("Project not found");
        }
        if(!traducteur.isPresent())
        {
            throw new RuntimeException("Translator not found");
        }
        User user=userService.getByIdByForce(projetTraducteurDto.getUserId());
        projet.get().setProjectStatus(ProjectStatus.IN_PROGRESS);
        projectRepository.save(projet.get());
        ProjetTraducteur projetTraducteur=new ProjetTraducteur();
        projetTraducteur.setTraducteur(traducteur.get());
        projetTraducteur.setProject(projet.get());
        projetTraducteur.setUser(user);
        activityService.logProjectActivity(user,ActivityType.PROJECT_ASSIGNED,projet.get().getTitle(),traducteur.get().getFullName(),projet.get().getId(),"Projet Attribué");
        return projetTraducteurRepository.save(projetTraducteur);
    }

    @Override
    public Optional<ProjetTraducteur> getbyProject(Long id) {
        Optional<ProjetTraducteur> projetTraducteur=projetTraducteurRepository.findByProjectId(id);
        if(projetTraducteur.isPresent())
        {
            return Optional.of(projetTraducteur.get());
        }else {
            return  null;
        }
    }

    @Override
    public Traducteur getTraducteurofProject(Project project) {
        Traducteur traducteur=new Traducteur();
        traducteur=null;
        Optional<ProjetTraducteur> projetTraducteur=this.getbyProject(project.getId());
        if(projetTraducteur!=null)
        {
            traducteur=projetTraducteur.get().getTraducteur();
        }
      return  traducteur;

    }

    @Override
    public List<Project> listOfTraducteur(Traducteur traducteur) {
        List<Project> projectList=new ArrayList<>();
        for(ProjetTraducteur projetTraducteur:projetTraducteurRepository.findByTraducteurId(traducteur.getId()))
        {
            projectList.add(projetTraducteur.getProject());
        }
        return projectList;
    }

    @Override
    public List<Project> listOfTraducteurCurentorEnd(Traducteur traducteur,Boolean end) {
        List<Project> projectList=new ArrayList<>();
        for(Project project:this.listOfTraducteur(traducteur))
        {
            if(project.getTerminer()==end)
            {
                projectList.add(project);
            }
        }
        return projectList;
    }

    @Override
    public Integer NumberofTraducteur(Traducteur traducteur, Boolean end) {
       Integer nb=0;
        for(Project project:this.listOfTraducteurCurentorEnd(traducteur,end))
        {
                nb++;
        }
        return nb;
    }
}
