package tunutech.api.services;

import tunutech.api.dtos.ProjetTraducteurDto;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetTraducteur;
import tunutech.api.model.Traducteur;

import java.util.List;
import java.util.Optional;

public interface ProjetTraducteurService {
    ProjetTraducteur create(ProjetTraducteurDto projetTraducteurDto);

    Optional<ProjetTraducteur> getbyProject(Long id);

    Traducteur getTraducteurofProject(Project project);

    List<Project> listOfTraducteur(Traducteur traducteur);
    List<Project> listOfTraducteurCurentorEnd(Traducteur traducteur,Boolean end);
    Integer NumberofTraducteur(Traducteur traducteur,Boolean end);
}
