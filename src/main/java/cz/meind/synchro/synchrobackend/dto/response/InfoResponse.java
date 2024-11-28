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

    public InfoResponse(List<String> users, List<String> shiftTypes) {
        this.users = users;
        this.shiftTypes = shiftTypes;
    }
}
