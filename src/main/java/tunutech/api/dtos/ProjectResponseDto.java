package tunutech.api.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tunutech.api.model.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@ToString
public class ProjectResponseDto {
    private Long id;
    private String code;
    private Date deadline;
    private ProjectStatus projectStatus;
    private TypeDocument typeDocument;
    private Float wordsCount;
    private Boolean annuler;
    private PriorityType priority;
    private String budget;
    private String description;
    private String title;
    private String sourceslangues;
    private String targetlangues;
    private  Double progress;
    private Boolean terminer;
    private Date startDate;
    private String identiteclient;
    private List<Document> documentlist;
    private List<Langue> languesources;
    private List<Langue> languetarget;
    private Client client;
    private Optional<Traducteur> translator;
    private Date assignedDate;
    private LocalDateTime lastActivity;
    private Float estimatedPrice;
    private Float pricePerWord;
}
