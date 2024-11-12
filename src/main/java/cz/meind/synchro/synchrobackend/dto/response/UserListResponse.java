package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserListResponse {
    private List<UserResponseEntity> userList;

    public UserListResponse(List<UserResponseEntity> userList) {
        this.userList = userList;
    }
}
