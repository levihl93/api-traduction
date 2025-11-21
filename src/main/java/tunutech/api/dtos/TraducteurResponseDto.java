package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tunutech.api.model.Langue;

import java.util.List;

@Setter
@Getter
@ToString
public class TraducteurResponseDto {
    private Long id;
    private String identite;
    private String textlangues;
    private Integer nbproject;
    private Long iduser;
    private List<Langue> langues;
}
