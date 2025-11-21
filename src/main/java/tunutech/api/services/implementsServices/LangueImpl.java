package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.LangueDto;
import tunutech.api.model.Langue;
import tunutech.api.repositories.LangueRepository;
import tunutech.api.services.LangueService;

import java.util.List;
import java.util.Optional;

@Service
public class LangueImpl implements LangueService {

    @Autowired
    private LangueRepository langueRepository;
    @Override
    public List<Langue> listall() {
        return langueRepository.findAll();
    }

    @Override
    public List<Langue> listallpresent(Boolean present) {
        return langueRepository.findByPresentOrderByName(present);
    }

    @Override
    public List<Langue> listallactive(Boolean active) {
        return langueRepository.findByActive(active);
    }

    @Override
    public Langue getUnique(Long id) {
        Optional <Langue>langue=langueRepository.findById(id);
        if(langue.isPresent())
        { return langue.get();}throw new RuntimeException("Langue not found");
    }

    @Override
    public Langue getUniquebyCode(String code) {
        Optional <Langue>langue= langueRepository.findByCode(code);
        if(langue.isPresent())
        { return langue.get();}throw new RuntimeException("Langue not found");
    }

    @Override
    public Langue getUniquebyName(String name) {
        Optional <Langue>langue= langueRepository.findByName(name);
        if(langue.isPresent())
        { return langue.get();}throw new RuntimeException("Langue not found");
    }

    @Override
    public Boolean ifNameExist(String name) {
        Optional<Langue>langue=langueRepository.findByName(name);
        boolean res=false;
        if(langue.isPresent())
        {
            res=true;
        }
        return res;
    }

    @Override
    public Boolean ifCodeExist(String code) {
        Optional<Langue>langue=langueRepository.findByCode(code);
        boolean res=false;
        if(langue.isPresent())
        {
            res=true;
        }
        return res;
    }

    @Override
    public Boolean ifExist(Long id) {
        Optional<Langue>langue=langueRepository.findById(id);
        boolean res=false;
        if(langue.isPresent())
        {
            res=true;
        }
        return res;
    }

    @Override
    public Langue saveLangue(LangueDto langueDto) {
        Langue langue=new Langue();
        langue.setName(langueDto.getName());
        langue.setCode(langueDto.getCode());
        langue.setActive(true);
        langue.setPresent(true);
        return  langueRepository.save(langue);
    }

    @Override
    public Langue updateLangue(LangueDto langueDto) {
        Langue langue=getUnique(langueDto.getId());
        langue.setName(langueDto.getName());
        langue.setCode(langueDto.getCode());
        return langueRepository.save(langue);
    }

    @Override
    public Langue setEnableLangue(LangueDto langueDto) {
        Langue langue=getUnique(langueDto.getId());
        langue.setActive(langueDto.isActive());
        return langueRepository.save(langue);
    }

    @Override
    public Langue setPresentLangue(LangueDto langueDto) {
        Langue langue=getUnique(langueDto.getId());
        langue.setPresent(langueDto.isPresent());
        return langueRepository.save(langue);
    }

}
