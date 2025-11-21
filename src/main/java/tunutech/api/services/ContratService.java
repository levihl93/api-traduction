package tunutech.api.services;

import tunutech.api.dtos.ContratDTO;
import tunutech.api.model.Contrat;
import tunutech.api.model.Project;
import tunutech.api.model.User;

public interface ContratService {
    Contrat createContrat(Long projetId, User user,String complexity,String type, Integer nbjours);

    Contrat getById(Long contractId);

    Boolean ifExistforProject(Long projectId);

    Contrat getofProject(String code);
    Contrat getofProjectbyProjectId(Long projectId);

    void deleteByProjectId(long projectId);

    String generateCode(Project project);

    Contrat update(ContratDTO contratDTO);

    void effectuerCalculsAutomatiques(Contrat contrat,Project project);
}
