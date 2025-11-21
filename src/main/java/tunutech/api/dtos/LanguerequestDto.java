package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.Langue;

import java.util.List;

@Getter
@Setter
public class LanguerequestDto {
    private List<Langue> langueslist;
}
