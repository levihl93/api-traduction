package tunutech.api.services;

import tunutech.api.dtos.TraducteurDto;
import tunutech.api.dtos.TraducteurResponseDto;
import tunutech.api.model.Langue;
import tunutech.api.model.Traducteur;

import java.util.List;

public interface TraducteurService {

    List<Traducteur> getAllTraducteurs();

    List<Traducteur> getAllPresent();
    List<Traducteur> getAllActive();
    List<Traducteur> getAllDisponible(List<Langue> langueList);
    Boolean ifTraducteurEligibleLangue(Traducteur traducteur, Langue langue);
    List<Traducteur> getAllPresentAndActive();
    Boolean ifClientisPresent(String email);

    Traducteur getbyEmailbyForce(String email);

    Long numberTraducteur();
    Integer numberTraducteurPresent(Boolean present);
    Traducteur getUnique(Long id);

    Traducteur saveTraducteur(TraducteurDto traducteurDto);

    TraducteurResponseDto maptraducteur(Traducteur traducteur,Boolean end);

}
