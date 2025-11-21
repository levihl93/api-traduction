package tunutech.api.services.implementsServices;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.ActivityDTO;
import tunutech.api.exception.ActivityNotFoundException;
import tunutech.api.exception.ActivityServiceException;
import tunutech.api.exception.InvalidActivityDataException;
import tunutech.api.model.*;
import tunutech.api.repositories.ActivityRepository;
import tunutech.api.services.ActivityService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;

    // CORRECTION : Implémentation de toutes les méthodes de l'interface

    @Override
    public void logUserActivity(User user, ActivityType type, String description) {
        try {
            validateUser(user);
            validateDescription(description);
            logActivity(user, type, ActivityGroup.USER, description, null, user.getFullName(), null, new HashMap<>());
        } catch (Exception e) {
            throw new ActivityServiceException("Erreur lors du logging de l'activité utilisateur", e);
        }
    }

    @Override
    public void logProjectActivity(User user, ActivityType type, String projectName, String beneficiaire,Long projectId, String description) {
        try {
            validateUser(user);
            validateProjectData(projectName, projectId);
            validateDescription(description);
            logActivity(user, type, ActivityGroup.PROJECT, description, projectName,beneficiaire, projectId,new HashMap<>());
        } catch (Exception e) {
            throw new ActivityServiceException("Erreur lors du logging de l'activité projet", e);
        }
    }

    @Override
    public void logPaymentActivity(User user, ActivityType type, String projectName, Long projectId, Map<String, Object> metadata) {
        try {
            validateUser(user);
            validateProjectData(projectName, projectId);
            String description = generatePaymentDescription(type, user, projectName, metadata);
            logActivity(user, type, ActivityGroup.PAYMENT, description, projectName, "",projectId, metadata);
        } catch (Exception e) {
            throw new ActivityServiceException("Erreur lors du logging de l'activité de paiement", e);
        }
    }

    @Override
    public void logSystemActivity(ActivityType type, String description, Map<String, Object> metadata) {
        try {
            Activity activity = Activity.builder()
                    .uuid(UUID.randomUUID().toString())
                    .type(type)
                    .category(ActivityGroup.SYSTEM)
                    .userId(0L) // ID système
                    .userName("Système")
                    .userEmail("system@tunutech.com")
                    .userRole(RoleUser.ADMIN)
                    .title(generateSystemActivityTitle(type))
                    .description(description)
                    .metadata(metadata != null ? metadata : new HashMap<>())
                    .isRead(false)
                    .priority(determinePriority(type))
                    .build();

            activityRepository.save(activity);
            log.info("System activity logged: {} - {}", type, description);
        } catch (Exception e) {
            throw new ActivityServiceException("Erreur lors du logging de l'activité système", e);
        }
    }

    // CORRECTION : Ajout de la méthode logActivity manquante
    private void logActivity(User user, ActivityType type, ActivityGroup category,
                             String description, String projectName, String beneficiaire,Long projectId,
                             Map<String, Object> metadata) {

        Map<String, Object> finalMetadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();

        Activity activity = Activity.builder()
                .uuid(UUID.randomUUID().toString())
                .type(type)
                .category(category)
                .userId(user.getId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .userRole(user.getRoleUser())
                .beneficiaire(beneficiaire)
                .title(generateActivityTitle(type, user, projectName))
                .description(description)
                .projectId(projectId)
                .projectName(projectName)
                .metadata(finalMetadata)
                .isRead(false)
                .priority(determinePriority(type))
                .build();

        activityRepository.save(activity);
        log.info("Activity logged: {} - {}", type, description);
    }

    // CORRECTION : Ajout de la méthode getActivityById manquante
    @Override
    public ActivityDTO getActivityById(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        return convertToDTO(activity);
    }

    // CORRECTION : Ajout de la méthode convertToDTO manquante
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setUuid(activity.getUuid());
        dto.setType(activity.getType());
        dto.setCategory(activity.getCategory());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setUserId(activity.getUserId());
        dto.setUserName(activity.getUserName());
        dto.setUserEmail(activity.getUserEmail());
        dto.setUserRole(activity.getUserRole());
        dto.setProjectId(activity.getProjectId());
        dto.setProjectName(activity.getProjectName());
        dto.setBeneficiaire(activity.getBeneficiaire());
        dto.setMetadata(activity.getMetadata());
        dto.setIsRead(activity.getIsRead());
        dto.setPriority(activity.getPriority());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        return dto;
    }

    // Méthodes de génération de titres et descriptions
    private String generateActivityTitle(ActivityType type, User user, String projectName) {
        String textuser;
        if(user.getRoleUser()== RoleUser.valueOf("CLIENT"))
        {
            textuser="Nouveau Client";
        }else {
            if(user.getRoleUser()== RoleUser.valueOf("TRANSLATOR"))
            {
                textuser="Nouveau Traducteur";
            }else {
                textuser="Nouveau Administrateur";
            }
        }
        switch (type) {
            case USER_REGISTERED:
                return String.format(textuser);
            case USER_UPDATED:
                return String.format("Profil mis à jour: %s", user.getFullName());
                case CONTRACT_GENERATED:
                return String.format("Contrat généré pour le projet: %s", projectName);
            case CONTRACT_ACCEPTED:
                return String.format("Contrat approuvé pour le projet: %s", projectName);
            case PROJECT_CREATED:
                return String.format("Nouveau projet de Traduction : %s", projectName);
            case PROJECT_UPDATED:
                return String.format("Projet de Traduction modifié : %s", projectName);
                case PROJECT_ASSIGNED:
                return String.format("Projet de Traduction attribué à :");
            case PROJECT_COMPLETED:
                return String.format("Projet terminé: %s", projectName);
            case TRANSLATION_STARTED:
                return String.format("Traduction débutée: %s", projectName);
            case TRANSLATION_COMPLETED:
                return String.format("Traduction terminée: %s", projectName);
            case PAYMENT_PROCESSED:
                return String.format("Paiement traité: %s", projectName);
            case PAYMENT_FAILED:
                return String.format("Échec de paiement: %s", projectName);
            default:
                return "Activité enregistrée";
        }
    }

    private String generateSystemActivityTitle(ActivityType type) {
        switch (type) {
            case SYSTEM_NOTIFICATION:
                return "Notification système";
            case BACKUP_CREATED:
                return "Sauvegarde créée";
            case ADMIN_ACTION:
                return "Action administrateur";
            default:
                return "Activité système";
        }
    }

    private String generatePaymentDescription(ActivityType type, User user, String projectName, Map<String, Object> metadata) {
        Double amount = (Double) metadata.get("amount");
        String currency = (String) metadata.get("currency");
        String paymentMethod = (String) metadata.get("paymentMethod");

        switch (type) {
            case PAYMENT_PROCESSED:
                return String.format("Paiement de %s %s traité via %s pour le projet '%s' par %s",
                        amount, currency, paymentMethod, projectName, user.getFullName());
            case PAYMENT_FAILED:
                return String.format("Échec du paiement de %s %s pour le projet '%s' par %s",
                        amount, currency, projectName, user.getFullName());
            case PAYMENT_REFUNDED:
                return String.format("Remboursement de %s %s pour le projet '%s'",
                        amount, currency, projectName);
            default:
                return "Activité de paiement";
        }
    }

    private PriorityType determinePriority(ActivityType type) {
        switch (type) {
            case PAYMENT_FAILED:
            case SYSTEM_NOTIFICATION:
                return PriorityType.HIGH;
            case PROJECT_CREATED:
            case TRANSLATION_COMPLETED:
            case PAYMENT_PROCESSED:
                return PriorityType.MEDIUM;
            default:
                return PriorityType.LOW;
        }
    }

    // Méthodes de récupération d'activités
    @Override
    public Page<ActivityDTO> getRecentActivities(Pageable pageable) {
        return activityRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<ActivityDTO> getUnreadActivities() {
        return activityRepository.findByIsReadFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActivityDTO> getUserActivities(Long userId, Pageable pageable) {
        return activityRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<ActivityDTO> getProjectActivities(Long projectId, Pageable pageable) {
        return activityRepository.findByProjectIdOrderByCreatedAtDesc(projectId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<ActivityDTO> getActivitiesByType(ActivityType type) {
        return activityRepository.findByTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> getRecentActivities(int limit) {
        return activityRepository.findRecentActivities(limit)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDTO> getRecentActivitiesOfProject(int limit, Long idproject) {
        return activityRepository.findRecentActivitiesofProject(idproject,limit)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Méthodes de gestion du statut de lecture
    @Override
    public void markAsRead(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        try {
            activity.setIsRead(true);
            activityRepository.save(activity);
            log.debug("Activity {} marked as read", activityId);
        } catch (Exception e) {
            throw new ActivityServiceException("Erreur lors du marquage de l'activité comme lue", e);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Activity> unreadActivities = activityRepository.findByUserIdAndIsReadFalse(userId);
        if (!unreadActivities.isEmpty()) {
            unreadActivities.forEach(activity -> activity.setIsRead(true));
            activityRepository.saveAll(unreadActivities);
            log.debug("Marked {} activities as read for user {}", unreadActivities.size(), userId);
        }
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return activityRepository.countByUserIdAndIsReadFalse(userId);
    }

    // Méthodes statistiques
    @Override
    public Long getTotalActivitiesCount() {
        return activityRepository.count();
    }

    @Override
    public Map<String, Long> getActivitiesCountByType() {
        List<Object[]> results = activityRepository.countActivitiesByType();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((ActivityType) result[0]).name(),
                        result -> (Long) result[1]
                ));
    }

    @Override
    public Map<String, Long> getActivitiesCountByCategory() {
        List<Object[]> results = activityRepository.countActivitiesByCategory();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> ((ActivityGroup) result[0]).name(),
                        result -> (Long) result[1]
                ));
    }

    // Méthodes de suppression
    @Override
    public void deleteActivity(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ActivityNotFoundException(activityId);
        }

        try {
            activityRepository.deleteById(activityId);
            log.info("Activity {} deleted", activityId);
        } catch (Exception e) {
            throw new ActivityServiceException("Erreur lors de la suppression de l'activité", e);
        }
    }

    @Override
    public void deleteOldActivities(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        int deletedCount = activityRepository.deleteByCreatedAtBefore(cutoffDate);
        log.info("Deleted {} activities older than {} days", deletedCount, days);
    }

    // Méthodes de validation
    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidActivityDataException("L'utilisateur ne peut pas être null");
        }
        if (user.getId() == null) {
            throw new InvalidActivityDataException("L'ID de l'utilisateur ne peut pas être null");
        }
    }

    private void validateProjectData(String projectName, Long projectId) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new InvalidActivityDataException("Le nom du projet ne peut pas être vide");
        }
        if (projectId == null) {
            throw new InvalidActivityDataException("L'ID du projet ne peut pas être null");
        }
    }


    private void validateContractProjectData(String projectName, Long contractId) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new InvalidActivityDataException("Le nom du projet ne peut pas être vide");
        }
        if (contractId == null) {
            throw new InvalidActivityDataException("L'ID du contrat ne peut pas être null");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidActivityDataException("La description ne peut pas être vide");
        }
    }
}