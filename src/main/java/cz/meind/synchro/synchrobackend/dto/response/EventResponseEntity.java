package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class EventResponseEntity {

    public EventResponseEntity(Long id, Timestamp timeStart, Timestamp timeEnd, String username, String type) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.username = username;
        this.type = type;
    }

    private Long id;

    private Timestamp timeStart;

    private Timestamp timeEnd;

    private String username;

    private String type;

}
