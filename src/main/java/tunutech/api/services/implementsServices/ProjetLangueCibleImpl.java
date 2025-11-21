package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tunutech.api.dtos.ProjetLangueCibleDto;
import tunutech.api.dtos.ProjetLangueSourceDto;
import tunutech.api.model.Langue;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetLangueCible;
import tunutech.api.model.ProjetLangueSource;
import tunutech.api.repositories.ProjetLangueCibleRepository;
import tunutech.api.repositories.ProjetLangueSourceRepository;
import tunutech.api.services.LangueService;
import tunutech.api.services.ProjetLangueCibleService;
import tunutech.api.services.ProjetService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetLangueCibleImpl implements ProjetLangueCibleService {
    @Autowired
    private ProjetLangueSourceRepository projetLangueSourceRepository;

    @Autowired
    private ProjetLangueCibleRepository projetLangueCibleRepository;
    @Autowired
    private LangueService langueService;

    @Override
    public List<Langue> Listofproject(Long idproject) {
        List<ProjetLangueCible> list=projetLangueCibleRepository.findByProjectId(idproject);
        List<Langue> resultats = new ArrayList<>();
        for(ProjetLangueCible projetLangueSource:list)
        {
            Langue langue=langueService.getUnique(projetLangueSource.getLangue().getId());
            resultats.add(langue);
        }
        return resultats;
    }

    @Override
    public ProjetLangueCible add(ProjetLangueCibleDto projetLangueSourceDto) {
            if(!this.ifExist(projetLangueSourceDto.getProject().getId(),projetLangueSourceDto.getIdlangue()))
            {
                Langue langue=langueService.getUnique(projetLangueSourceDto.getIdlangue());
                ProjetLangueCible projetLangueSource=new ProjetLangueCible();
                projetLangueSource.setLangue(langue);
                projetLangueSource.setProject(projetLangueSourceDto.getProject());
                return projetLangueCibleRepository.save(projetLangueSource);
            } throw  new RuntimeException("language exist for this Project");

    }

    @Override
    public Optional<ProjetLangueCible> getofProjetLangue(Long idprojet, Long idlangue) {
        return projetLangueCibleRepository.findByProjectIdAndLangueId(idprojet,idlangue);
    }

    @Override
    @Transactional
    public void deleteallofProject(Project project) {
        projetLangueCibleRepository.deleteAllByProject(project);
    }

    @Override
    public Boolean ifExist(Long idproject, Long idlangue) {
        Boolean res=false;
        Optional<ProjetLangueCible> projetLangueCible=this.getofProjetLangue(idproject,idlangue);
        if(projetLangueCible.isPresent())
        {
            res=true;
        }
        return res;
    }
}
