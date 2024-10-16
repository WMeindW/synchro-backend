package cz.meind.synchro.synchrobackend.service.events;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class EventTypesService {
    @PostConstruct
    private void initializeTypes() {
        System.out.println("Initilaized");
    }
}
