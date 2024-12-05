package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummaryResponse {
    private List<String> usernames;
    private List<String> calculatedValues;
    private List<String> advertisedValues;

    public SummaryResponse(List<String> usernames, List<String> calculatedValues, List<String> advertisedValues) {
        this.usernames = usernames;
        this.calculatedValues = calculatedValues;
        this.advertisedValues = advertisedValues;
    }
}
