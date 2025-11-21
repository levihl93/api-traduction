package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.Client;
import tunutech.api.model.RoleUser;
import tunutech.api.model.Traducteur;

import java.util.Date;

@Setter
@Getter
public class ProfilDto {
    private String nom;
    private Long id;
    private String prenoms;
    private String denomination;
    private Traducteur traducteur;
    private Client client;
    private String identite;
    private String sexe;
    private Date joindate;
    private String pays;
    private String email;
    private String telephone;
    private String adresse;
    private String secteur;
    private String type;
    private RoleUser roleUser;
    private Boolean actif;
    private Long totalProjects;
}