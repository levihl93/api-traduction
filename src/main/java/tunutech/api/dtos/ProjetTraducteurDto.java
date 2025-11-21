package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProjetTraducteurDto {
    private Long traducteurId;
    private Long projectId;
    private Long userId;
}
