package cz.meind.synchro.synchrobackend.service.events;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.EventTypeRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditEventDto;
import cz.meind.synchro.synchrobackend.dto.response.EventResponseEntity;
import cz.meind.synchro.synchrobackend.dto.response.EventsResponse;
import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

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
        if (!hasPermissions(request, createEventDto.getUsername())) return false;
        if (eventTypesService.checkMissing(createEventDto.getType())) return false;
        saveEvent(createEventDto);
        return true;
    }

    public boolean editEvent(EditEventDto editEventDto, String role, HttpServletRequest request) {
        if (!checkEditEvent(editEventDto)) return false;
        if (!hasPermissions(request, editEventDto.getUsername())) return false;
        if (eventTypesService.checkMissing(editEventDto.getType())) return false;
        saveEditEvent(editEventDto);
        return true;
    }


    public boolean deleteEvent(EditEventDto editEventDto, String role, HttpServletRequest request) {
        if (!validationUtil.loginCheck(editEventDto.getUsername())) return false;
        if (!hasPermissions(request, editEventDto.getUsername())) return false;
        if (eventTypesService.checkMissing(editEventDto.getType())) return false;
        deleteEvent(editEventDto.getUsername(), editEventDto.getId());
        return true;
    }

    public EventsResponse queryEvents() {
        List<EventResponseEntity> responseEntities = eventRepository.findAll().stream().filter(eventEntity -> !eventEntity.isDeleted()).map(eventEntity -> new EventResponseEntity(eventEntity.getId(), eventEntity.getTimeStart().toLocalDateTime(), eventEntity.getTimeEnd().toLocalDateTime(), eventEntity.getUser().getUsername(), eventEntity.getType().getName())).collect(Collectors.toList());
        return new EventsResponse(responseEntities);
    }

    private boolean hasPermissions(HttpServletRequest request, String username) {
        return jwtUtil.extractClaims(securityService.extractCookie(request)).getSubject().equals(username) || jwtUtil.extractClaims(securityService.extractCookie(request)).get("role").toString().equals(synchroConfig.getAdminRole());
    }

    @Async
    protected void deleteEvent(String username, Long id) {
        eventRepository.findAllByUser(userRepository.findByUsername(username).get()).forEach(eventEntity -> {
            if (id.equals(eventEntity.getId())) {
                eventRepository.updateEventEntityDeletedById(true, id);
            }
        });
    }

    @Async
    protected void saveEvent(CreateEventDto createEventDto) {
        eventRepository.save(new EventEntity(eventTypeRepository.findEventTypeEntityByName(createEventDto.getType()).get(), userRepository.findByUsername(createEventDto.getUsername()).get(), Timestamp.valueOf(createEventDto.getEnd()), Timestamp.valueOf(createEventDto.getStart())));
    }

    @Async
    protected void saveEditEvent(EditEventDto editEventDto) {
        eventRepository.updateEventEntityById(userRepository.findByUsername(editEventDto.getUsername()).get(), Timestamp.valueOf(editEventDto.getEnd()), Timestamp.valueOf(editEventDto.getStart()), eventTypeRepository.findEventTypeEntityByName(editEventDto.getType()).get(), editEventDto.getId());
    }

    private boolean checkEvent(CreateEventDto createEventDto) {
        if (!validationUtil.loginCheck(createEventDto.getUsername())) return false;
        return validationUtil.validateEvent(createEventDto);
    }

    private boolean checkEditEvent(EditEventDto editEventDto) {
        if (!validationUtil.loginCheck(editEventDto.getUsername())) return false;
        return validationUtil.validateEventEdit(editEventDto);
    }
}
