package tunutech.api.services;

import tunutech.api.dtos.AnalyticsRequestDto;
import tunutech.api.dtos.AnalyticsResponseDto;

public interface AnalyticsService {
    /**
     * Récupère toutes les données analytiques pour le tableau de bord
     * @param request DTO contenant les paramètres de la requête
     * @return Réponse contenant toutes les données analytiques
     */
    AnalyticsResponseDto getAnalyticsData(AnalyticsRequestDto request);

    /**
     * Récupère les données pour une période spécifique
     * @param period Période demandée (24h, 7j, 30j, 3m)
     * @return Réponse analytique
     */
    AnalyticsResponseDto getAnalyticsByPeriod(String period);

    /**
     * Récupère les données pour une période personnalisée
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Réponse analytique
     */
    AnalyticsResponseDto getAnalyticsByCustomPeriod(String startDate, String endDate);

    /**
     * Actualise les données en temps réel
     * @param request Paramètres de la requête
     * @return Données actualisées
     */
    AnalyticsResponseDto refreshAnalyticsData(AnalyticsRequestDto request);

    /**
     * Vérifie si le service est opérationnel
     * @return true si le service est disponible
     */
    boolean isServiceAvailable();
}
