package cz.meind.synchro.synchrobackend.service.events;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.EventTypeEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventTypesService {


    private final EventTypeRepository eventTypeRepository;
    private final SynchroConfig synchroConfig;

    public EventTypesService(EventTypeRepository eventTypeRepository, SynchroConfig synchroConfig) {
        this.eventTypeRepository = eventTypeRepository;
        this.synchroConfig = synchroConfig;
    }

    @PostConstruct
    private void initializeTypes() {
        for (String m : checkMissing()) eventTypeRepository.save(new EventTypeEntity(m));
    }

    private List<String> checkMissing() {
        List<String> missing = synchroConfig.getEventTypeList();
        missing.removeIf(e -> eventTypeRepository.findAll().stream().map(EventTypeEntity::getName).toList().contains(e));
        return missing;
    }
}
