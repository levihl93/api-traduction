// controller/AnalyticsController.java
package tunutech.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;;
import tunutech.api.dtos.AnalyticsRequestDto;
import tunutech.api.dtos.AnalyticsResponseDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import tunutech.api.services.AnalyticsService;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/analytics/")
@RequiredArgsConstructor
@Validated
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Endpoint principal pour récupérer toutes les données analytiques
     * GET /analytics/dashboard?period=7j
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsResponseDto> getDashboardAnalytics(
            @RequestParam String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("📊 Requête GET analytics - Période: {}, Start: {}, End: {}", period, startDate, endDate);

        // Validation manuelle de la période
        if (!isValidPeriod(period)) {
            return ResponseEntity.badRequest()
                    .body(AnalyticsResponseDto.error("La période doit être: 24h, 7j, 30j, 3m ou personnalisée"));
        }
        // ✅ CORRECTION: Validation spécifique pour "personnalise"
        if ("personnalise".equals(period)) {
            if (startDate == null || endDate == null) {
                return ResponseEntity.badRequest()
                        .body(AnalyticsResponseDto.error(
                                "Les dates de début et fin sont requises pour une période personnalisée"
                        ));
            }
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(AnalyticsResponseDto.error(
                                "La date de début doit être avant la date de fin"
                        ));
            }
        }

        AnalyticsRequestDto request = new AnalyticsRequestDto();
        request.setPeriod(period);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        AnalyticsResponseDto response = analyticsService.getAnalyticsData(request);

        log.info("✅ Données analytiques retournées avec succès - Période: {}", period);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint POST pour récupérer les données analytiques
     * POST /analytics/dashboard
     */
    @PostMapping("/dashboard")
    public ResponseEntity<AnalyticsResponseDto> getDashboardAnalyticsPost(
            @RequestBody AnalyticsRequestDto request) {

        log.info("📊 Requête POST analytics - Période: {}", request.getPeriod());

        // Validation manuelle de la période
        if (!isValidPeriod(request.getPeriod())) {
            return ResponseEntity.badRequest()
                    .body(AnalyticsResponseDto.error("La période doit être: 24h, 7j, 30j ou 3m"));
        }

        AnalyticsResponseDto response = analyticsService.getAnalyticsData(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint rapide pour une période spécifique
     * GET /analytics/period/7j
     */
    @GetMapping("/period/{period}")
    public ResponseEntity<AnalyticsResponseDto> getAnalyticsByPeriod(
            @PathVariable String period) {

        log.info("🎯 Requête analytics par période - Période: {}", period);

        // Validation manuelle de la période
        if (!isValidPeriod(period)) {
            return ResponseEntity.badRequest()
                    .body(AnalyticsResponseDto.error("La période doit être: 24h, 7j, 30j, 3m ou personnalise"));
        }

        AnalyticsResponseDto response = analyticsService.getAnalyticsByPeriod(period);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour une période personnalisée
     * GET /analytics/custom?startDate=2024-01-01&endDate=2024-01-07
     */
    @GetMapping("/custom")
    public ResponseEntity<AnalyticsResponseDto> getAnalyticsByCustomPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("📅 Requête analytics période personnalisée - Start: {}, End: {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .body(AnalyticsResponseDto.error("La période doit être: 24h, 7j, 30j, 3m ou personnalise"));
        }

        AnalyticsResponseDto response = analyticsService.getAnalyticsByCustomPeriod(
                startDate.toString(), endDate.toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour forcer l'actualisation des données
     * POST /analytics/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AnalyticsResponseDto> refreshAnalyticsData(
            @RequestBody AnalyticsRequestDto request) {

        log.info("🔄 Actualisation des données analytics - Période: {}", request.getPeriod());

        // Validation manuelle de la période
        if (!isValidPeriod(request.getPeriod())) {
            return ResponseEntity.badRequest()
                    .body(AnalyticsResponseDto.error("La période doit être: 24h, 7j, 30j, 3m ou personnalise"));
        }

        AnalyticsResponseDto response = analyticsService.refreshAnalyticsData(request);

        log.info("✅ Données actualisées avec succès");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de santé du service
     * GET /analytics/health
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        log.info("❤️ Health check analytics service");

        boolean isHealthy = analyticsService.isServiceAvailable();
        HealthResponse healthResponse = new HealthResponse(
                "Analytics Service",
                isHealthy ? "UP" : "DOWN",
                isHealthy ? "Service opérationnel" : "Service indisponible",
                LocalDate.now().toString()
        );

        return ResponseEntity.ok(healthResponse);
    }

    /**
     * Endpoint pour les statistiques globales (résumé)
     * GET /analytics/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<AnalyticsResponseDto> getSummary() {
        log.info("📈 Requête summary analytics");

        // Par défaut, on retourne les 7 derniers jours
        AnalyticsResponseDto response = analyticsService.getAnalyticsByPeriod("7j");

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour les métriques en temps réel (24h)
     * GET /analytics/realtime
     */
    @GetMapping("/realtime")
    public ResponseEntity<AnalyticsResponseDto> getRealtimeMetrics() {
        log.info("⚡ Requête métriques temps réel");

        AnalyticsResponseDto response = analyticsService.getAnalyticsByPeriod("24h");

        return ResponseEntity.ok(response);
    }

    /**
     * Méthode utilitaire pour valider la période
     */
    private boolean isValidPeriod(String period) {
        return period != null && period.matches("24h|7j|30j|3m|personnalise");
    }

    // Classe interne pour la réponse de santé
    private static class HealthResponse {
        private String service;
        private String status;
        private String message;
        private String timestamp;

        public HealthResponse(String service, String status, String message, String timestamp) {
            this.service = service;
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters
        public String getService() { return service; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }

    /**
     * Gestionnaire d'exceptions pour les validations
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AnalyticsResponseDto> handleValidationException(IllegalArgumentException ex) {
        log.warn("❌ Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(AnalyticsResponseDto.error(ex.getMessage()));
    }

    /**
     * Gestionnaire d'exceptions générales
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AnalyticsResponseDto> handleGeneralException(Exception ex) {
        log.error("💥 Erreur inattendue: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(AnalyticsResponseDto.error("Erreur interne du serveur"));
    }
}