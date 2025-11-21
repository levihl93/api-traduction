package tunutech.api.services;

import tunutech.api.dtos.LangueDto;
import tunutech.api.model.Langue;

import java.util.List;

public interface LangueService {

    List<Langue> listall();

    List<Langue> listallpresent(Boolean present);
    List<Langue> listallactive(Boolean present);

    Langue getUnique(Long id);
    Langue getUniquebyCode(String code);
    Langue getUniquebyName(String name);

    Boolean ifNameExist(String name);
    Boolean ifCodeExist(String code);
    Boolean ifExist(Long id);

    Langue saveLangue(LangueDto langueDto);

    Langue updateLangue(LangueDto langueDto);

    Langue setEnableLangue(LangueDto langueDto);
    Langue setPresentLangue(LangueDto langueDto);

}
