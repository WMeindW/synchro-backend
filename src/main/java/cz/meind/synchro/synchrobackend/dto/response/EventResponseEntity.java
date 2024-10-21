package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class EventResponseEntity {

    public EventResponseEntity(Timestamp timeStart, Timestamp timeEnd, String username, String type) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.username = username;
        this.type = type;
    }

    private Timestamp timeStart;

    private Timestamp timeEnd;

    private String username;

    private String type;

}
