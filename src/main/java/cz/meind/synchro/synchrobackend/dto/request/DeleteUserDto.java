package cz.meind.synchro.synchrobackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserDto {
    private String id;

    private String username;

    @Override
    public String toString() {
        return "DeleteUserDto{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
