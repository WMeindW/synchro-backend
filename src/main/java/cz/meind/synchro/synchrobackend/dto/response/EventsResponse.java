package cz.meind.synchro.synchrobackend.dto.response;

import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventsResponse {
    private List<EventEntity> events;
}
