package tunutech.api.services;

import org.springframework.stereotype.Service;
import tunutech.api.dtos.TraducteurLangueDto;
import tunutech.api.model.Langue;
import tunutech.api.model.TraducteurLangue;

import java.util.List;
import java.util.Optional;

@Service
public interface TraducteurLangueService {
    List<TraducteurLangue> listall();
    List<TraducteurLangue> getOfTraducteur(Long idtraducteur);
    List<Langue> getOfTraducteursLanguages(Long idtraducteur);
    TraducteurLangue getUnique(Long id);
    Optional<TraducteurLangue> getofTraducteurLangue(Long idtraducteur, Long idlangue);
    Boolean ifExist(Long idtraducteur, Long idlangue);
    TraducteurLangue createTraducteurLangue(TraducteurLangueDto traducteurLangueDto);

    void setLangues(Long id, List<Langue> langueList);
    void delete(Long id);
}
