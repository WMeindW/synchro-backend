package cz.meind.synchro.synchrobackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserDto {
    private String username;

    private String role;

    private String email;

    private String phone;

    private String password;

    private Long id;

    @Override
    public String toString() {
        return "EditUserDto{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
