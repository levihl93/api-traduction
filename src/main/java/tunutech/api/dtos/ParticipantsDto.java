package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParticipantsDto {
    private Long id;
    private String name;
    private String role; // "client", "translator", "admin"
    private String avatar;
}
