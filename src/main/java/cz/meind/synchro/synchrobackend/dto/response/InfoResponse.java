package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InfoResponse {
    private final List<String> users;
    private final List<String> shiftTypes;
    private final List<String> userTypes;

    public InfoResponse(List<String> users, List<String> shiftTypes, List<String> userTypes) {
        this.users = users;
        this.shiftTypes = shiftTypes;
        this.userTypes = userTypes;
    }
}
