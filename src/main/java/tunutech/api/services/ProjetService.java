package tunutech.api.services;

import tunutech.api.dtos.ProjectDto;
import tunutech.api.dtos.ProjectResponseDto;
import tunutech.api.dtos.TranslationProjectsDto;
import tunutech.api.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjetService {
    List<Project> listall();
    List<Project> listallofClientPresent();
    Double calculerMontantAutomatique(Project projet, String projectComplexity,String documentType);

    Long NumberofProject();
    List<Project>listvalider(Boolean valider);
    List<Project>listofPeriode(LocalDate d1,LocalDate d2);
    String getLanguesSources(Project project);
    List<Langue> getLanguesSourcesLangues(Project project);
    String getLanguesCibles(Project project);
    List<Langue> getLanguesCiblesLangues(Project project);
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
