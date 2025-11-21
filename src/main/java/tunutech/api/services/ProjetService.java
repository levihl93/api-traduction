package tunutech.api.services;

import tunutech.api.dtos.ProjectDto;
import tunutech.api.dtos.ProjectResponseDto;
import tunutech.api.dtos.TranslationProjectsDto;
import tunutech.api.model.Project;
import tunutech.api.model.ProjectComplexity;
import tunutech.api.model.ProjetTraducteur;
import tunutech.api.model.Traducteur;

import java.util.List;
import java.util.Optional;

public interface ProjetService {
    List<Project> listall();
    Double calculerMontantAutomatique(Project projet, String projectComplexity,String documentType);

    Long NumberofProject();
    List<Project>listvalider(Boolean valider);
    String getLanguesSources(Project project);
    String getLanguesCibles(Project project);
    List<Project> ListofClient(Long idclient);
    List<Project> ListofTraducteur(Long idtraducteur);

    List<Project>Listterminer(Boolean terminer);

    List<Project>ListterminerofClient(Long idclient);

    Project saveproject(ProjectDto projectDto);

    Project getUniquebyId(Long id);

    Project getUniquebyCode(String code);

    String generateCode(Long idclient);

    ProjectResponseDto mapProject(Project project);

    TranslationProjectsDto bigMap(List<ProjectResponseDto> list);


    Project update(ProjectDto projectDto);

    Optional<Traducteur> getTraducteorofProject(Project project);
}
