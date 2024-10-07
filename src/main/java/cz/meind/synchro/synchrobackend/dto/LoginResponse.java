package cz.meind.synchro.synchrobackend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String token;

    private long expiresIn;

    private String role;

}