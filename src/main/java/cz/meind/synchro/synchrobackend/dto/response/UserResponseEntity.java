package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseEntity {
    private String id;

    private String username;

    private String role;

    private String email;

    private String phone;

    private String enabled;

    public UserResponseEntity(String id, String username, String role, String email, String phone, String enabled) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
    }
}
