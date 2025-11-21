package tunutech.api.controller;

import lombok.Data;
import tunutech.api.model.Contrat;

@Data
public class ContratExistResponseDTO {
    private  Boolean contratexiste;
    private Contrat contrat;
}
