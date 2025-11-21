package tunutech.api.services;

import tunutech.api.dtos.ProjetLangueSourceDto;
import tunutech.api.model.Langue;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetLangueSource;

import java.util.List;
import java.util.Optional;

public interface ProjetLangueSourceService {
    List<Langue> Listofproject(Long idproject);

    ProjetLangueSource add(ProjetLangueSourceDto projetLangueSourceDto);

    Optional<ProjetLangueSource> getofProjetLangue(Long idprojet, Long idlangue);

    Boolean ifExist(Long idproject, Long idlangue);

    void deleteAllOfProject(Project project);
}
