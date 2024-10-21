package cz.meind.synchro.synchrobackend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventsResponse {
    private List<EventResponseEntity> events;

    public EventsResponse(List<EventResponseEntity> events) {
        this.events = events;
    }
}

