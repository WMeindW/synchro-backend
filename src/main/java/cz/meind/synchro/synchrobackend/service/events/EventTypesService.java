package cz.meind.synchro.synchrobackend.service.events;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.EventTypeEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing event types. It interacts with the repository to check and initialize event types in the system.
 */
@Service
public class EventTypesService {

    private final EventTypeRepository eventTypeRepository;
    private final SynchroConfig synchroConfig;

    /**
     * Constructs an instance of EventTypesService with the specified repository and configuration.
     *
     * @param eventTypeRepository The repository for accessing event type data.
     * @param synchroConfig       The configuration containing a list of event types.
     */
    public EventTypesService(EventTypeRepository eventTypeRepository, SynchroConfig synchroConfig) {
        this.eventTypeRepository = eventTypeRepository;
        this.synchroConfig = synchroConfig;
    }

    /**
     * Checks if a specific event type is missing in the database.
     *
     * @param type The name of the event type to check.
     * @return true if the event type is missing, false otherwise.
     */
    public boolean checkMissing(String type) {
        return eventTypeRepository.findEventTypeEntityByName(type).isEmpty();
    }

    /**
     * Initializes event types that are missing from the database. This method is called automatically after the bean is created.
     * It checks for missing event types from the configuration and saves them in the repository.
     */
    @PostConstruct
    private void initializeTypes() {
        // Saves missing event types
        for (String m : checkMissing()) {
            eventTypeRepository.save(new EventTypeEntity(m));
        }
    }

    /**
     * Checks for event types that are missing from the database by comparing the configured event types with the ones already saved.
     *
     * @return A list of event types that are missing from the database.
     */
    private List<String> checkMissing() {
        // List of configured event types
        List<String> missing = new ArrayList<>(synchroConfig.getEventTypeList());

        // Remove event types that are already present in the database
        missing.removeIf(e -> eventTypeRepository.findAll().stream().map(EventTypeEntity::getName).toList().contains(e));

        return missing;
    }
}

