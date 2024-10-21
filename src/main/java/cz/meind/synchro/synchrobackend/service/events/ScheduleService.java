package cz.meind.synchro.synchrobackend.service.events;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.EventTypeRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ScheduleService {

    private final ValidationUtil validationUtil;
    private final SynchroConfig synchroConfig;
    private final JwtUtil jwtUtil;
    private final SecurityService securityService;
    private final EventTypesService eventTypesService;
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserRepository userRepository;

    public ScheduleService(ValidationUtil validationUtil, SynchroConfig synchroConfig, JwtUtil jwtUtil, SecurityService securityService, EventTypesService eventTypesService, EventRepository eventRepository, EventTypeRepository eventTypeRepository, UserRepository userRepository) {
        this.validationUtil = validationUtil;
        this.synchroConfig = synchroConfig;
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
        this.eventTypesService = eventTypesService;
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.userRepository = userRepository;
    }

    public boolean createEvent(CreateEventDto createEventDto, String role, HttpServletRequest request) {
        if (!checkEvent(createEventDto)) return false;
        if (eventTypesService.checkMissing(createEventDto.getType())) return false;
        /*if (role.equals(synchroConfig.getCombinedRole()) && !jwtUtil.extractClaims(securityService.extractCookie(request)).getSubject().equals(createEventDto.getUsername()))
            return false;
         */
        return saveEvent(createEventDto);
    }

    private boolean saveEvent(CreateEventDto createEventDto) {
        eventRepository.save(new EventEntity(
                eventTypeRepository.findEventTypeEntityByName(createEventDto.getType()).get(),
                userRepository.findByUsername(createEventDto.getUsername()).get(),
                Timestamp.valueOf(createEventDto.getEnd()),
                Timestamp.valueOf(createEventDto.getStart())));
        return true;
    }

    private boolean checkEvent(CreateEventDto createEventDto) {
        if (!validationUtil.loginCheck(createEventDto.getUsername())) return false;
        return validationUtil.validateEvent(createEventDto);
    }
}
