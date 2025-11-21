package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.Langue;
import tunutech.api.model.Traducteur;

@Getter
@Setter
public class TraducteurLangueDto {
    private Long idtraducteur;
    private long idlangue;
}
