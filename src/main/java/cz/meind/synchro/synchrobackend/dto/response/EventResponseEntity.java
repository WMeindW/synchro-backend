package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
public class EventResponseEntity {

    public EventResponseEntity(Long id, LocalDateTime timeStart, LocalDateTime timeEnd, String username, String type) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.username = username;
        this.type = type;
    }

    private Long id;

    private LocalDateTime timeStart;

    private LocalDateTime timeEnd;

    private String username;

    private String type;

}
