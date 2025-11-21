package tunutech.api.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ContratResponseDTO {
    private Long id;
    private String clientNom;
    private String clientEmail;
    private String clientAdresse;
    private Long userId;
    private String clientPays;
    private String codeContrat;
    private String traducteurNom;
    private String langueSource;
    private String langueCible;
    private Float nombreMots;
    private Integer nombreJours;
    private Boolean accepted;
    private String typeDocument;
    private String description;
    private Double montantPropose;
    private String devise;
    private Double majorationPercentage;
    private Double montantMajoration;
    private Date dateEcheanceProposee;
    private LocalDateTime dateSignature;
    private Date dateCreation;
    private String conditionsGenerales;
    private String conditionsSpeciales;
    private String statut;

    // Formattage pour l'affichage
    private String montantFormate;
    private String majorationFormate;
    private String dateEcheanceFormatee;
    private String dateSignatureFormatee;
    private String dateCreationFormatee;
    private String nombreMotsFormate;
    private String niveauComplexite;
}
