package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tunutech.api.dtos.ProjetLangueSourceDto;
import tunutech.api.model.Langue;
import tunutech.api.model.Project;
import tunutech.api.model.ProjetLangueSource;
import tunutech.api.repositories.ProjetLangueSourceRepository;
import tunutech.api.services.LangueService;
import tunutech.api.services.ProjetLangueSourceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetLangueSourceImpl implements ProjetLangueSourceService {
    @Autowired
    private ProjetLangueSourceRepository projetLangueSourceRepository;
    @Autowired
    private LangueService langueService;
    @Override
    public List<Langue> Listofproject(Long idproject) {
        List<ProjetLangueSource> list=projetLangueSourceRepository.findByProjectId(idproject);
        List<Langue> resultats = new ArrayList<>();
        for(ProjetLangueSource projetLangueSource:list)
        {
            Langue langue=langueService.getUnique(projetLangueSource.getLangue().getId());
            resultats.add(langue);
        }
        return resultats;
    }

    @Override
    public ProjetLangueSource add(ProjetLangueSourceDto projetLangueSourceDto) {
            if(!this.ifExist(projetLangueSourceDto.getProject().getId(),projetLangueSourceDto.getIdlangue()))
            {
                Langue langue=langueService.getUnique(projetLangueSourceDto.getIdlangue());
                ProjetLangueSource projetLangueSource=new ProjetLangueSource();
                projetLangueSource.setLangue(langue);
                projetLangueSource.setProject(projetLangueSourceDto.getProject());
                return projetLangueSourceRepository.save(projetLangueSource);
            } throw  new RuntimeException("language exist for this Project");

    }

    @Override
    public Optional<ProjetLangueSource> getofProjetLangue(Long idprojet, Long idlangue) {
        return projetLangueSourceRepository.findByProjectIdAndLangueId(idprojet,idlangue);
    }

    @Override
    public Boolean ifExist(Long idproject, Long idlangue) {
        Boolean res=false;
        Optional<ProjetLangueSource> projetLangueSource=this.getofProjetLangue(idproject,idlangue);
        if(projetLangueSource.isPresent())
        {
            res=true;
        }
        return res;
    }

    @Override
    @Transactional
    public void deleteAllOfProject(Project project) {
        projetLangueSourceRepository.deleteAllByProject(project);
    }
}
