package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserValueResponseEntity {

    private String username;

    private String advertisedValue;

    private String calculatedValue;

    public UserValueResponseEntity(String username, String advertisedValue, String calculatedValue) {
        this.username = username;
        this.advertisedValue = advertisedValue;
        this.calculatedValue = calculatedValue;
    }
}
