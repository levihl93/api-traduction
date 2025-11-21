package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import tunutech.api.model.User;

@Getter
@Setter
@Accessors(chain = true) // ✅ rend les setters chaînables
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserDto userDto;
}
