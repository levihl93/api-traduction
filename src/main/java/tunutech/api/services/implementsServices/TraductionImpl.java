package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.TraducteurDto;
import tunutech.api.dtos.TraducteurResponseDto;
import tunutech.api.model.ActivityType;
import tunutech.api.model.Langue;
import tunutech.api.model.Traducteur;
import tunutech.api.model.User;
import tunutech.api.repositories.TraducteurLangueRepository;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.ActivityService;
import tunutech.api.services.ProjetTraducteurService;
import tunutech.api.services.TraducteurLangueService;
import tunutech.api.services.TraducteurService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TraductionImpl implements TraducteurService {

    @Autowired
    private TraducteurRepository traducteurRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraducteurLangueService traducteurLangueService;
    @Autowired
    private TraducteurLangueRepository traducteurLangueRepository;

    @Autowired
    private ProjetTraducteurService projetTraducteurService;

    @Override
    public List<Traducteur> getAllTraducteurs() {
        return traducteurRepository.findAll(Sort.by(Sort.Direction.ASC,"lastname"));
    }

    @Override
    public List<Traducteur> getAllPresent() {
        return traducteurRepository.findByPresent(true);
    }

    @Override
    public List<Traducteur> getAllActive() {
        return traducteurRepository.findByActive(true);
    }

    @Override
    public List<Traducteur> getAllDisponible(List<Langue> langueList) {
        List<Traducteur> traducteurList=new ArrayList<>();
        for(Traducteur traducteur:this.getAllActive())
        {
            for(Langue langue:langueList)
            {
                if(this.ifTraducteurEligibleLangue(traducteur,langue))
                {
                    if(!traducteurList.stream().anyMatch(trad->trad.getId().equals(traducteur.getId())))//tester la presence
                    {
                        traducteurList.add(traducteur);
                    }
                }
            }

        }
        return traducteurList;
    }

    @Override
    public Boolean ifTraducteurEligibleLangue(Traducteur traducteur, Langue langue) {
        Boolean result=false;
        if(traducteurLangueRepository.findByTraducteurIdAndLangueId(traducteur.getId(), langue.getId()).isPresent())
        {
            result=true;
        }
        return result;
    }

    @Override
    public List<Traducteur> getAllPresentAndActive() {
        return traducteurRepository.findByPresentAndActive(true,true);
    }

    @Override
    public Boolean ifClientisPresent(String email) {
        Optional<Traducteur>traducteur= Optional.ofNullable(traducteurRepository.findByEmail(email));
        boolean res=false;
        if(traducteur.isPresent())
        {
            res=true;
        }
        return res;
    }


    @Override
    public Traducteur getbyEmailbyForce(String email) {
        return traducteurRepository.findByEmail(email);
    }

    @Override
    public Long numberTraducteur() {
        return traducteurRepository.count();
    }

    @Override
    public Integer numberTraducteurPresent(Boolean present) {
        return traducteurRepository.countByPresent(present);
    }

    @Override
    public Traducteur getUnique(Long id) {
        Optional<Traducteur>traducteur=traducteurRepository.findById(id);
        if(traducteur.isPresent())
        {
            return traducteur.get();
        }throw new RuntimeException("Translator not found");
    }

    @Override
    public Traducteur saveTraducteur(TraducteurDto traducteurDto) {
        Traducteur traducteur=new Traducteur();
        traducteur.setEmail(traducteurDto.getEmail());
        traducteur.setPays(traducteurDto.getPays());
        traducteur.setFirstname(traducteurDto.getFirstname());
        traducteur.setLastname(traducteurDto.getLastname());
        traducteur.setAdresse(traducteurDto.getAdresse());
        traducteur.setSexe(traducteurDto.getSexe());
        traducteur.setTelephone(traducteurDto.getTelephone());
        traducteur.setActive(false);
        traducteur.setPresent(true);
        traducteur.setAvailable(true);
        Traducteur traducteursaved=traducteurRepository.save(traducteur);
        traducteurRepository.save(traducteur);
        return traducteursaved;
    }

    @Override
    public TraducteurResponseDto maptraducteur(Traducteur traducteur,Boolean end) {

            List<Langue> langueList=traducteurLangueService.getOfTraducteursLanguages(traducteur.getId());
            TraducteurResponseDto traducteurResponseDto=new TraducteurResponseDto();
            traducteurResponseDto.setId(traducteur.getId());
            Optional<User> user=userRepository.findByTraducteurId(traducteur.getId());
            if(user.isPresent())
            {
                traducteurResponseDto.setIduser(user.get().getId());
            }
            traducteurResponseDto.setNbproject(projetTraducteurService.NumberofTraducteur(traducteur,end));
            traducteurResponseDto.setIdentite(traducteur.getFirstname()+' '+traducteur.getLastname());
            String languestexte="";
            Integer intr=0;
            Integer nbsources=langueList.size();
            for(Langue langue:langueList)
            {
                languestexte+=langue.getName();
                intr++;
                if(intr<nbsources)
                {
                    languestexte+=";";
                }
            }
            traducteurResponseDto.setTextlangues(languestexte);
            traducteurResponseDto.setLangues(langueList);
            traducteurResponseDto.setId(traducteur.getId());

        return traducteurResponseDto;
    }
}
