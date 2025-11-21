package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tunutech.api.model.PriorityType;
import tunutech.api.model.TypeDocument;

import java.util.Date;

@Getter
@Setter
@ToString
public class ProjectDto {
    private String code;
    private String title;
    private String description;
    private  Float priceperWord;
    private Float estimatedPrice;
    private Long idclient;
    private Float wordscount;
    private PriorityType priorityType;
    private TypeDocument typeDocument;
    private Date datevoulue;
    private Boolean state;
}
