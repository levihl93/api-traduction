package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.Project;

@Getter
@Setter
public class ProjetLangueSourceDto{
    private Long idlangue;
    private Project project;
}
