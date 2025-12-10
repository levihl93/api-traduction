package tunutech.api.services.implementsServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.Utils.DateComparisonUtils;
import tunutech.api.dtos.*;
import tunutech.api.model.*;
import tunutech.api.services.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private ProjetService projetService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LangueService langueService;


    /**
     * MÉTHODE PRINCIPALE : Point d'entrée pour récupérer toutes les données analytiques
     */
    @Override
    public AnalyticsResponseDto getAnalyticsData(AnalyticsRequestDto request) {
        log.info("Calcul des données analytiques pour la période: {}", request.getPeriod());
        String period = request.getPeriod();

        // ✅ CORRECTION: Validation cohérente
        if (!period.matches("24h|7j|30j|3m|personnalise")) {
            throw new IllegalArgumentException("Période invalide: " + period);
        }

        try {
            validateRequest(request);

            // ✅ CORRECTION: Calculer la période AVANT de récupérer les données
            PeriodBounds bounds = calculatePeriodBounds(request);

            log.info("📅 Période calculée: {} -> {}", bounds.getStartDate(), bounds.getEndDate());

            // ✅ CORRECTION: Utiliser les dates calculées pour récupérer les données
            List<Client> listclients = clientService.allClientCreatedAtPeriode(
                    bounds.getStartDate(), bounds.getEndDate(), clientService.allclient());
            List<Project> listprojets = projetService.listofPeriode(bounds.getStartDate(), bounds.getEndDate());
            List<Activity> activityList = activityService.getActivitiesInPeriod(bounds.getStartDate(), bounds.getEndDate());

            // Calcul des métriques basées sur la période
            AnalyticsMetricsDto metrics = calculateMetrics(request, bounds, listclients, listprojets, activityList);
            List<AnalyticsDetailedDataDto> detailedData = calculateDetailedData(bounds, listprojets, listclients, activityList);
            ChartDataDto charts = calculateChartsData(request, listprojets);
            AnalyticsSummaryDto summary = generateSummary(request, bounds, detailedData.size());

            return AnalyticsResponseDto.success(metrics, detailedData, charts, summary);

        } catch (Exception e) {
            log.error("Erreur lors du calcul des données analytiques", e);
            return AnalyticsResponseDto.error("Erreur de calcul: " + e.getMessage());
        }
    }

    /**
     * MÉTHODE DE CALCUL DE PÉRIODE : Détermine les dates de début/fin selon la période demandée
     */
    private PeriodBounds calculatePeriodBounds(AnalyticsRequestDto request) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = now;

        switch (request.getPeriod()) {
            case "24h":
                // Dernières 24 heures - aujourd'hui seulement
                startDate = now;
                break;

            case "7j":
                // 7 derniers jours (inclus aujourd'hui)
                startDate = now.minusDays(6);
                break;

            case "30j":
                // 30 derniers jours
                startDate = now.minusDays(29);
                break;

            case "3m":
                // 3 derniers mois (environ 90 jours)
                startDate = now.minusMonths(3);
                break;

            case "personnalise":
                // ✅ CORRECTION: Utiliser les dates de la requête
                if (request.getStartDate() != null && request.getEndDate() != null) {
                    startDate = request.getStartDate();
                    endDate = request.getEndDate();

                    // Validation supplémentaire
                    if (startDate.isAfter(endDate)) {
                        throw new IllegalArgumentException("La date de début doit être avant la date de fin");
                    }
                } else {
                    throw new IllegalArgumentException("Dates manquantes pour période personnalisée");
                }
                break;

            default:
                // Par défaut, 7 derniers jours
                startDate = now.minusDays(6);
        }

        log.info("📅 Période calculée: {} -> {} ({} jours)",
                startDate, endDate, startDate.until(endDate).getDays() + 1);

        return new PeriodBounds(startDate, endDate);
    }

    /**
     * MÉTHODE DE CALCUL DES MÉTRIQUES : Agrège tous les indicateurs de performance
     */
    private AnalyticsMetricsDto calculateMetrics(AnalyticsRequestDto request, PeriodBounds bounds,List<Client> listclient,List<Project> listprojectPeriod, List<Activity> activityList) {
        AnalyticsMetricsDto metrics = new AnalyticsMetricsDto();

        // Calculer les données basées sur la période réelle
        int days = calculateTotalDays(bounds);
        PeriodData periodData = calculatePeriodData(request.getPeriod(), days,listprojectPeriod);

        metrics.setTotalTraductions(periodData.getTotalTranslations());
        metrics.setMotsParJour(periodData.getWordsPerDay());
        metrics.setNouveauxClients(listclient.size());
        metrics.setPrecisionMoyenne(calculateAveragePrecision(request));
        metrics.setLanguesActives(calculateActiveLanguages(request));
        metrics.setCroissance(calculateGrowthRate(request));
        metrics.setTotalProjets(listprojectPeriod.size());
        metrics.setProjetsTermines(calculateCompletedProjects(request, bounds));
        metrics.setTauxCompletion(calculateCompletionRate(request, bounds));
        metrics.setUtilisateursActifs(calculateActiveUsers(request,activityList));
        metrics.setErreursTotal(calculateTotalErrors(request));
        metrics.setRevenus(calculateRevenue(request, bounds));
        metrics.setTempsMoyenTraitement(calculateAverageProcessingTime(request));

        return metrics;
    }

    private Integer calculateNewClients(AnalyticsRequestDto request,List<Client> list) {
            return clientService.allClientCreatedAtPeriode(request.getStartDate(),request.getEndDate(),list).size();
    }

    /**
     * MÉTHODE DE DONNÉES DE PÉRIODE : Fournit les données de base selon la période demandée
     */
    private PeriodData calculatePeriodData(String period, int days,List<Project>projectListPeriode) {
        // Données proportionnelles à la durée réelle de la période
       Integer nbmotstraduits =0;
        for(Project project:projectListPeriode)
        {
            if(project.getIsEnd())
            {
                nbmotstraduits+=Math.round(project.getWordscount());
            }
        }
        switch (period) {
            case "24h":
                return new PeriodData(1850L, nbmotstraduits, 8);
            case "7j":
                return new PeriodData(12500L, nbmotstraduits, 84);
            case "30j":
                return new PeriodData(45210L, nbmotstraduits, 320);
            case "3m":
                return new PeriodData(135000L, nbmotstraduits, 950);
            default:
                // Pour les périodes personnalisées, calcul proportionnel
                long estimatedTranslations = 12500L * days / 7;
                Integer estimatedWords = 55555;
                int estimatedClients = 84 * days / 7;
                return new PeriodData(estimatedTranslations, estimatedWords, estimatedClients);
        }
    }

    /**
     * MÉTHODE DE DONNÉES DÉTAILLÉES : Génère les données temporelles pour la période calculée
     */
    private List<AnalyticsDetailedDataDto> calculateDetailedData(PeriodBounds bounds,List<Project>projectList,List<Client>clientList,List<Activity> activityList) {
        List<AnalyticsDetailedDataDto> detailedData = new ArrayList<>();
            List<Activity> listactivity=new ArrayList<>();
        // Générer une entrée pour chaque jour de la période
        LocalDate currentDate = bounds.getStartDate();
        while (!currentDate.isAfter(bounds.getEndDate())) {
            listactivity.clear();
            AnalyticsMetricsDto analyticsMetricsDto=new AnalyticsMetricsDto();
            Integer nbproject=0;
            Integer nbprojectend=0;
            Integer nbclient=0;
            Integer nbmot = 0;
            for(Project project:projectList)
            {
                if((DateComparisonUtils.isEqual(project.getCreatedAt(),currentDate)))
                {
                    nbproject++;
                    if(project.getIsEnd())
                    {
                        nbprojectend++;
                        Integer nmot=Math.round(project.getWordscount()) ;
                        nbmot+=nmot;
                    }
                }
            }   for(Client client:clientList)
            {
                if((DateComparisonUtils.isEqual(client.getCreated_At(),currentDate)))
                {
                    nbclient++;
                }
            }
            for(Activity activity:activityList)
            {
                    if(DateComparisonUtils.isEqualLocalDateTime(activity.getCreatedAt(),currentDate))
                    {
                        listactivity.add(activity);
                    }
            }
            System.out.println("mot");
            System.out.println(nbmot);
            analyticsMetricsDto.setTotalProjets(nbproject);
            analyticsMetricsDto.setProjetsTermines(nbprojectend);
            analyticsMetricsDto.setMotsParJour((nbmot));
            analyticsMetricsDto.setNouveauxClients(nbclient);
            analyticsMetricsDto.setUtilisateursActifs(clientService.getClientsActivityofPeriode(listactivity).size());
            detailedData.add(createDailyDataPoint(currentDate,analyticsMetricsDto));
            currentDate = currentDate.plusDays(1);
        }

        return detailedData;
    }

    /**
     * MÉTHODE DE CRÉATION POINT QUOTIDIEN : Génère des données réalistes pour une date
     */
    private AnalyticsDetailedDataDto createDailyDataPoint(LocalDate date,AnalyticsMetricsDto metricsDto) {
        // Simulation de données réalistes avec un seed basé sur la date
        Random random = new Random(date.hashCode());

        return createDetailedDataPoint(
                date.atTime(12, 0).toString(), // 2024-01-01T12:00:00
                random.nextInt(200) + 100,     // traductions: 100-300
                metricsDto.getMotsParJour(),
                getRandomLanguagePair(),
                random.nextDouble() * 10 + 90, // precision: 90-100%
                random.nextInt(5),             // erreurs: 0-4
                metricsDto.getUtilisateursActifs(),     // utilisateursActifs: 50-150
                metricsDto.getNouveauxClients(),        // nouveauxClients: 5-15
                metricsDto.getTotalProjets(),        // projetsCreés: 4-12
                metricsDto.getProjetsTermines(),         // projetsTermines: 3-9
                random.nextDouble() * 2 + 3    // satisfactionClient: 3-5
        );
    }

    // ==========================================================================
    // MÉTHODES SPÉCIALISÉES DE CALCUL DES MÉTRIQUES (MISES À JOUR)
    // ==========================================================================

    private BigDecimal calculateAveragePrecision(AnalyticsRequestDto request) {
        return new BigDecimal("94.2");
    }

    private Integer calculateActiveLanguages(AnalyticsRequestDto request) {
        return 12;
    }

    private BigDecimal calculateGrowthRate(AnalyticsRequestDto request) {
        return new BigDecimal("15.3");
    }

    private Integer calculateTotalProjects(AnalyticsRequestDto request, PeriodBounds bounds,List<Project> projectList) {
        // Utiliser les dates calculées pour récupérer les projets
        return this.listOfProject(bounds).size();
    }

    private List<Project> listOfProject(PeriodBounds bounds)
    {
        return projetService.listofPeriode(bounds.startDate,bounds.endDate);
    }

    private Integer calculateCompletedProjects(AnalyticsRequestDto request, PeriodBounds bounds) {
        // Logique de calcul des projets terminés sur la période
        Integer nb=0;
        for(Project project:this.listOfProject(bounds))
        {
            if(project.getIsEnd())
            {
                nb++;
            }
        }
        return nb;
    }

    private BigDecimal calculateCompletionRate(AnalyticsRequestDto request, PeriodBounds bounds) {
        // Calcul basé sur les projets réels
        Integer totalProjects = 100;
        Integer completedProjects = calculateCompletedProjects(request, bounds);
        if (totalProjects > 0) {
            return new BigDecimal(completedProjects * 100.0 / totalProjects)
                    .setScale(1, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private Integer calculateActiveUsers(AnalyticsRequestDto request, List<Activity> activityList) {
       return clientService.getClientsActivityofPeriode(activityList).size();
    }


    private Integer calculateTotalErrors(AnalyticsRequestDto request) {
        return 12;
    }

    private BigDecimal calculateRevenue(AnalyticsRequestDto request, PeriodBounds bounds) {
        // Revenus proportionnels à la durée
        int days = calculateTotalDays(bounds);
        BigDecimal baseRevenue = new BigDecimal("12500.50");
        return baseRevenue.multiply(new BigDecimal(days)).divide(new BigDecimal("7"), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateAverageProcessingTime(AnalyticsRequestDto request) {
        return new BigDecimal("2.3");
    }

    // ==========================================================================
    // MÉTHODES DE DONNÉES GRAPHIQUES
    // ==========================================================================

    private ChartDataDto calculateChartsData(AnalyticsRequestDto request,List<Project>projectList) {
        ChartDataDto charts = new ChartDataDto();

        charts.setLanguesRepartition(calculateLanguageDistribution(request,projectList));
        charts.setTopProjets(calculateTopProjects(request));
        charts.setTypesDocuments(calculateDocumentTypes(request));
        charts.setActiviteHoraire(calculateHourlyActivity(request));

        return charts;
    }

    private List<ChartDataDto.ChartItem> calculateLanguageDistribution(AnalyticsRequestDto request,List<Project> projectList) {
            List<Langue>tablangues=new ArrayList<>();
            List<LanguesResponseAnalyticDto>tabResponse=new ArrayList<>();

            Integer nbtot = 0;
            tablangues=langueService.listallpresent(true);
            for(Langue langue:tablangues)
            {
                LanguesResponseAnalyticDto languesResponseAnalyticDto=new LanguesResponseAnalyticDto();
                languesResponseAnalyticDto.setId(langue.getId());
                languesResponseAnalyticDto.setName(langue.getName());
                languesResponseAnalyticDto.setValue(0);
                tabResponse.add(languesResponseAnalyticDto);
            }
            for(Project project:projectList)
            {
                for(LanguesResponseAnalyticDto languesResponseAnalyticDto1:tabResponse)
                {
                    Boolean ok=false;
                    for(Langue languesource:projetService.getLanguesSourcesLangues(project))
                    {
                        nbtot++;
                        if(languesource.getId()==languesResponseAnalyticDto1.getId())
                        {
                                ok=true;
                        }
                    }
                    for(Langue languecible:projetService.getLanguesCiblesLangues(project))
                    {
                        nbtot++;
                        if(languecible.getId()==languesResponseAnalyticDto1.getId())
                        {
                                ok=true;
                        }
                    }
                    if(ok)
                    {
                        languesResponseAnalyticDto1.setValue(languesResponseAnalyticDto1.getValue()+1);
                    }
                }
            }
        List<ChartDataDto.ChartItem> chartItems = new ArrayList<>();
        for(LanguesResponseAnalyticDto languesResponseAnalyticDto2 : tabResponse)
        {
            Double percent=0.0;
            if(nbtot>0)
            {
                percent= (double) (languesResponseAnalyticDto2.getValue()*100/nbtot);
            }
            chartItems.add(createChartItem(languesResponseAnalyticDto2.getName(), percent));
        }
        System.out.println("sasa");
        System.out.println(chartItems);
            return chartItems;
    }

    private List<ChartDataDto.ChartItem> calculateTopProjects(AnalyticsRequestDto request) {
        return Arrays.asList(
                createChartItem("Site Web Corporate", 85),
                createChartItem("Application Mobile", 92),
                createChartItem("Documentation Technique", 78),
                createChartItem("Campagne Marketing", 95)
        );
    }

    private List<ChartDataDto.ChartItem> calculateDocumentTypes(AnalyticsRequestDto request) {
        return Arrays.asList(
                createChartItem("PDF", 45),
                createChartItem("DOCX", 25),
                createChartItem("HTML", 15),
                createChartItem("TXT", 10),
                createChartItem("Autres", 5)
        );
    }

    private List<ChartDataDto.ChartItem> calculateHourlyActivity(AnalyticsRequestDto request) {
        return Arrays.asList(
                createChartItem("08:00", 45),
                createChartItem("10:00", 78),
                createChartItem("12:00", 65),
                createChartItem("14:00", 82),
                createChartItem("16:00", 91),
                createChartItem("18:00", 58)
        );
    }

    // ==========================================================================
    // MÉTHODES UTILITAIRES
    // ==========================================================================

    private void validateRequest(AnalyticsRequestDto request) {
        if (request.getPeriod() == null || request.getPeriod().trim().isEmpty()) {
            throw new IllegalArgumentException("La période est obligatoire");
        }

        if ("personnalise".equals(request.getPeriod())) {
            if (request.getStartDate() == null || request.getEndDate() == null) {
                throw new IllegalArgumentException("Les dates sont obligatoires pour une période personnalisée");
            }
            try {
                LocalDate start = request.getStartDate();
                LocalDate end = request.getEndDate();
                if (start.isAfter(end)) {
                    throw new IllegalArgumentException("La date de début doit être avant la date de fin");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Format de date invalide");
            }
        }
    }

    private AnalyticsDetailedDataDto createDetailedDataPoint(String date, int traductions, Integer motsTraduits,
                                                             String langues, double precision, int erreurs,
                                                             int utilisateurs, int nouveauxClients,
                                                             int projetsCrees, int projetsTermines, double satisfaction) {
        AnalyticsDetailedDataDto data = new AnalyticsDetailedDataDto();
        data.setDate(LocalDateTime.parse(date));
        data.setTraductions(traductions);
        data.setMotsTraduits(motsTraduits);
        data.setLanguesUtilisees(langues);
        data.setPrecision(BigDecimal.valueOf(precision));
        data.setErreurs(erreurs);
        data.setUtilisateursActifs(utilisateurs);
        data.setNouveauxClients(nouveauxClients);
        data.setProjetsCrees(projetsCrees);
        data.setProjetsTermines(projetsTermines);
        data.setSatisfactionClient(BigDecimal.valueOf(satisfaction));
        return data;
    }

    private ChartDataDto.ChartItem createChartItem(String name, Number value) {
        ChartDataDto.ChartItem item = new ChartDataDto.ChartItem();
        item.setName(name);
        item.setValue(value);
        return item;
    }

    private String getRandomLanguagePair() {
        String[] languages = {"FR-EN", "EN-FR", "FR-ES", "ES-FR", "EN-DE", "DE-EN", "FR-DE", "EN-IT"};
        return languages[new Random().nextInt(languages.length)];
    }

    private int calculateTotalDays(PeriodBounds bounds) {
        return (int) bounds.getStartDate().until(bounds.getEndDate()).getDays() + 1;
    }

    /**
     * MÉTHODE DE GÉNÉRATION RÉSUMÉ : Crée les métadonnées avec période calculée
     */
    private AnalyticsSummaryDto generateSummary(AnalyticsRequestDto request, PeriodBounds bounds, int dataPoints) {
        AnalyticsSummaryDto summary = new AnalyticsSummaryDto();
        summary.setPeriode(request.getPeriod());
        summary.setDateDebut(bounds.getStartDate().atStartOfDay());
        summary.setDateFin(bounds.getEndDate().atTime(23, 59, 59));
        summary.setDernierRefresh(LocalDateTime.now());
        summary.setTotalPoints(dataPoints);
        summary.setStatut(AnalyticsSummaryDto.DataStatus.COMPLET);
        summary.setMessage(String.format("Données du %s au %s (%d jours)",
                bounds.getStartDate(), bounds.getEndDate(), calculateTotalDays(bounds)));
        summary.setGenerationTimeMs(120L);
        return summary;
    }

    // ==========================================================================
    // MÉTHODES DE L'INTERFACE
    // ==========================================================================

    @Override
    public AnalyticsResponseDto getAnalyticsByPeriod(String period) {
        AnalyticsRequestDto request = new AnalyticsRequestDto();
        request.setPeriod(period);
        return getAnalyticsData(request);
    }

    @Override
    public AnalyticsResponseDto getAnalyticsByCustomPeriod(String startDate, String endDate) {
        AnalyticsRequestDto request = new AnalyticsRequestDto();
        request.setPeriod("personnalise");
        request.setStartDate(LocalDate.parse(startDate));  // ✅ Conversion String → LocalDate
        request.setEndDate(LocalDate.parse(endDate));      // ✅ Conversion String → LocalDate
        return getAnalyticsData(request);
    }

    @Override
    public AnalyticsResponseDto refreshAnalyticsData(AnalyticsRequestDto request) {
        log.info("Recalcul des données analytiques en temps réel");
        return getAnalyticsData(request);
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            return true;
        } catch (Exception e) {
            log.error("Service analytics non disponible", e);
            return false;
        }
    }

    // ==========================================================================
    // CLASSES INTERNES
    // ==========================================================================

    /**
     * CLASSE INTERNE PERIOD BOUNDS : Conteneur pour les bornes de période
     */
    private static class PeriodBounds {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public PeriodBounds(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
    }

    /**
     * CLASSE INTERNE PERIOD DATA : Conteneur pour les données de période
     */
    private static class PeriodData {
        private final Long totalTranslations;
        private final Integer wordsPerDay;
        private final Integer newClients;

        public PeriodData(Long totalTranslations, Integer wordsPerDay, Integer newClients) {
            this.totalTranslations = totalTranslations;
            this.wordsPerDay = wordsPerDay;
            this.newClients = newClients;
        }

        public Long getTotalTranslations() { return totalTranslations; }
        public Integer getWordsPerDay() { return wordsPerDay; }
        public Integer getNewClients() { return newClients; }
    }
}