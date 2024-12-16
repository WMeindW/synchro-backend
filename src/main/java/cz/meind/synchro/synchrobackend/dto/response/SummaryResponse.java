package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummaryResponse {
    private List<UserValueResponseEntity> values;

    public SummaryResponse(List<UserValueResponseEntity> values) {
        this.values = values;
    }
}
