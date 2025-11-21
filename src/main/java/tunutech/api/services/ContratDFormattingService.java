package tunutech.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.ContratResponseDTO;
import tunutech.api.model.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Service
public class ContratDFormattingService {

    @Autowired
    private ProjetService projetService;
    public ContratResponseDTO formaterContratPourAffichage(Contrat contrat) {
        ContratResponseDTO dto = new ContratResponseDTO();


        // Copie des données de base
        dto.setId(contrat.getId());
        dto.setClientNom(contrat.getClientName());
        dto.setClientEmail(contrat.getClientEmail());
        dto.setClientAdresse(contrat.getClientAdresse());
        dto.setClientPays(contrat.getClientPays());
        dto.setLangueSource(projetService.getLanguesSources(contrat.getProject()));
        dto.setLangueCible(projetService.getLanguesCibles(contrat.getProject()));
        dto.setAccepted(contrat.getContratStatut()== ContratStatut.ACCEPTE? true:false);
        dto.setUserId(contrat.getUser().getId());
        dto.setCodeContrat(contrat.getCode());
        dto.setNombreJours(contrat.getNombreJours());
        dto.setDescription(contrat.getProject().getDescription());
        dto.setNombreMots(contrat.getNombreMots());
        dto.setTypeDocument(String.valueOf(contrat.getProject().getTypeDocument()));
        dto.setMontantPropose(contrat.getMontantContrat());
        dto.setDevise(String.valueOf(contrat.getDevise()));
        dto.setMajorationPercentage(contrat.getMajorationPourcentage());
        dto.setNiveauComplexite(contrat.getProjectComplexity().toString());
        dto.setMontantMajoration(contrat.getMontatMajoration());
        dto.setDateEcheanceProposee(contrat.getEcheanceContrat());
        dto.setDateSignature(contrat.getApproved_At());
        dto.setDateCreation(contrat.getCreated_At());
        dto.setConditionsGenerales(contrat.getConditionsGenerales());
        dto.setConditionsSpeciales(contrat.getConditionsSpeciales());
        dto.setStatut(String.valueOf(contrat.getContratStatut()));

        // Formattage pour l'affichage
        dto.setMontantFormate(formatMontant(contrat.getMontantContrat(), contrat.getDevise()));
        dto.setMajorationFormate(formatMontant(contrat.getMontatMajoration(), contrat.getDevise()));
        dto.setDateEcheanceFormatee(formatDate(contrat.getEcheanceContrat()));
        dto.setDateSignatureFormatee(formatDateTime(dto.getDateSignature()));
        dto.setDateCreationFormatee(formatDate(contrat.getCreated_At()));
        dto.setNombreMotsFormate(formatNombreMots(contrat.getProject().getWordscount()));
        return dto;
    }
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null; // ou return "Aucune date";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);
        return formatter.format(dateTime);
    }

    private String formatMontant(Double montant, Devise devise) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(montant) + " " + devise;
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
        return formatter.format(date);
    }

    private String formatNombreMots(Float nombreMots) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(nombreMots);
    }
}
