package tunutech.api.dtos;

import lombok.Data;
import tunutech.api.model.ContratStatut;

@Data
public class ContratDTO {
    private Long id;
    private long projectId;
    private long userId;
    private String complexity;
    private  Integer nbjours;
    private ContratStatut contratStatut;
    private String type;
}
