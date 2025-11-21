package tunutech.api.services;

import tunutech.api.dtos.ProjetLangueCibleDto;
import tunutech.api.dtos.ProjetLangueSourceDto;
import tunutech.api.model.Langue;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetLangueCible;
import tunutech.api.model.ProjetLangueSource;

import java.util.List;
import java.util.Optional;

public interface ProjetLangueCibleService {
    List<Langue> Listofproject(Long idproject);

    ProjetLangueCible add(ProjetLangueCibleDto projetLangueCibleDto);

    Optional<ProjetLangueCible> getofProjetLangue(Long idprojet, Long idlangue);

    void deleteallofProject(Project project);

    Boolean ifExist(Long idproject, Long idlangue);

}
