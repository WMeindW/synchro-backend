package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseEntity {
    private String id;

    private String username;

    private String role;

    private String enabled;

    public UserResponseEntity(String id, String username, String role, String enabled) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
    }
}
