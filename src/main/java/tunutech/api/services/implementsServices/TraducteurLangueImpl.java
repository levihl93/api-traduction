package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.TraducteurLangueDto;
import tunutech.api.model.Langue;
import tunutech.api.model.Traducteur;
import tunutech.api.model.TraducteurLangue;
import tunutech.api.repositories.LangueRepository;
import tunutech.api.repositories.TraducteurLangueRepository;
import tunutech.api.repositories.TraducteurRepository;
import tunutech.api.services.LangueService;
import tunutech.api.services.TraducteurLangueService;
import tunutech.api.services.TraducteurService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TraducteurLangueImpl implements TraducteurLangueService {
    @Autowired
    private TraducteurLangueRepository traducteurLangueRepository;
    @Autowired
    private LangueService langueService;
    @Autowired
    private TraducteurRepository traducteurRepository;

    @Override
    public List<TraducteurLangue> listall() {
        return traducteurLangueRepository.findAll();
    }

    @Override
    public List<TraducteurLangue> getOfTraducteur(Long idtraducteur) {
        return traducteurLangueRepository.findByTraducteurId(idtraducteur);
    }

    @Override
    public List<Langue> getOfTraducteursLanguages(Long idtraducteur) {
        List<TraducteurLangue>list=this.getOfTraducteur(idtraducteur);
        List<Langue> langueList=new ArrayList<>();
        for(TraducteurLangue traducteurLangue: list)
        {
           langueList.add(traducteurLangue.getLangue());
        }
        return langueList;
    }

    @Override
    public TraducteurLangue getUnique(Long id) {
        Optional<TraducteurLangue>traducteurLangue=traducteurLangueRepository.findById(id);
        if(traducteurLangue.isPresent())
        {
            return traducteurLangue.get();
        } throw  new RuntimeException("Not found");

    }

    @Override
    public Optional<TraducteurLangue> getofTraducteurLangue(Long idtraducteur, Long idlangue) {
        return traducteurLangueRepository.findByTraducteurIdAndLangueId(idtraducteur,idlangue);
    }

    @Override
    public Boolean ifExist(Long idtraducteur, Long idlangue) {
        Optional <TraducteurLangue> traducteurLangue=traducteurLangueRepository.findByTraducteurIdAndLangueId(idtraducteur,idlangue);
        boolean res=false;
        if(traducteurLangue.isPresent())
        {
            res=true;
        }
        return res;
    }

    @Override
    public TraducteurLangue createTraducteurLangue(TraducteurLangueDto traducteurLangueDto) {
        TraducteurLangue traducteurLangue=new TraducteurLangue();
        Optional<Traducteur> traducteur=traducteurRepository.findById(traducteurLangueDto.getIdtraducteur());
        if(traducteur.isPresent())
        {
            traducteurLangue.setTraducteur(traducteur.get());
        }
        Langue langue=langueService.getUnique(traducteurLangueDto.getIdlangue());

            traducteurLangue.setLangue(langue);
        return traducteurLangueRepository.save(traducteurLangue);
}

    @Override
    public void setLangues(Long id, List<Langue> langueList) {
        List<TraducteurLangue> list=this.getOfTraducteur(id);
        for(TraducteurLangue traducteurLangue:list)
        {
            this.delete(traducteurLangue.getId());
        }

        for(Langue langue:langueList)
        {
            TraducteurLangueDto traducteurLangueDto=new TraducteurLangueDto();
            traducteurLangueDto.setIdlangue(langue.getId());
            traducteurLangueDto.setIdtraducteur(id);
            this.createTraducteurLangue(traducteurLangueDto);
        }
    }

    @Override
    public void delete(Long id) {
        Optional<TraducteurLangue> traducteurLangue=traducteurLangueRepository.findById(id);
        traducteurLangueRepository.delete(traducteurLangue.get());
    }
}
