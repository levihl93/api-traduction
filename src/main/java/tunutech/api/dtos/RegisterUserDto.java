package tunutech.api.dtos;

import tunutech.api.model.Client;
import tunutech.api.model.RoleUser;

import java.util.Optional;

public class RegisterUserDto {
    private String email;
    private boolean admin;
    private RoleUser roleUser;

    public RoleUser getRoleUser() {
        return roleUser;
    }

    public void setRoleUser(RoleUser roleUser) {
        this.roleUser = roleUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;


    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    public Long getIdtraducteur() {
        return idtraducteur;
    }

    public void setIdtraducteur(Long idtraducteur) {
        this.idtraducteur = idtraducteur;
    }

    public Long getIdclient() {
        return idclient;
    }

    public void setIdclient(Long idclient) {
        this.idclient = idclient;
    }

    private Long idtraducteur;
    private Long idclient;

    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
