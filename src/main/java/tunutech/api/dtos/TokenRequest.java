package tunutech.api.dtos;

public class TokenRequest {
    private String token;

    // Constructeur par défaut (OBLIGATOIRE pour Jackson)
    public TokenRequest() {}

    // Constructeur avec paramètre
    public TokenRequest(String token) {
        this.token = token;
    }

    // Getter et Setter (OBLIGATOIRES)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // toString() optionnel mais utile
    @Override
    public String toString() {
        return "TokenRequest{token='" + token + "'}";
    }
}
