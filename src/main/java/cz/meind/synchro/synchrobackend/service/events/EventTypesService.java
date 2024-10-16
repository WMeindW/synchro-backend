package cz.meind.synchro.synchrobackend.service.events;

import cz.meind.synchro.synchrobackend.database.repositories.EventTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class EventTypesService {

    private final EventTypeRepository eventTypeRepository;

    public EventTypesService(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    @PostConstruct
    private void initializeTypes() {
        System.out.println("Initilaized");
    }
}
