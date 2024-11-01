package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String token;

    private long expiresIn;

    private String role;

    private String username;

    public LoginResponse(String token, long expiresIn, String role, String username) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.role = role;
        this.username = username;
    }
}