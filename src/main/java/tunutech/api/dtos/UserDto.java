package tunutech.api.dtos;

import tunutech.api.model.AvatarType;
import tunutech.api.model.SenderRole;

public class UserDto {
    private Long id;
    private String email;
    private String avatarUrl;
    private AvatarType avatarType;
    private String avatarInitialColor;
    private boolean admin;
    private boolean active;

    public boolean isPresent() {
        return present;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public AvatarType getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(AvatarType avatarType) {
        this.avatarType = avatarType;
    }

    public String getAvatarInitialColor() {
        return avatarInitialColor;
    }

    public void setAvatarInitialColor(String avatarInitialColor) {
        this.avatarInitialColor = avatarInitialColor;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    private boolean present;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private SenderRole senderRole; // "CLIENT", "TRADUCTEUR"
    private Long idClient;
    private Long idTraducteur;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public UserDto() {
    }

    public UserDto(Long id, String email) {
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public SenderRole getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(SenderRole senderRole) {
        this.senderRole = senderRole;
    }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }

    public Long getIdTraducteur() { return idTraducteur; }
    public void setIdTraducteur(Long idTraducteur) { this.idTraducteur = idTraducteur; }
}
