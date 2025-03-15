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
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing events, including creating, editing, deleting, and querying events.
 * It also validates event data and checks user permissions.
 */
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

    /**
     * Constructs an instance of ScheduleService with the specified dependencies.
     *
     * @param validationUtil Utility for validating data.
     * @param synchroConfig Configuration for the application.
     * @param jwtUtil Utility for handling JWTs.
     * @param securityService Service for managing security-related operations.
     * @param eventTypesService Service for handling event types.
     * @param eventRepository Repository for accessing event data.
     * @param eventTypeRepository Repository for accessing event type data.
     * @param userRepository Repository for accessing user data.
     */
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

    /**
     * Creates a new event based on the provided data.
     * It validates the event data, checks if the user has the necessary permissions, and ensures the event type exists.
     *
     * @param createEventDto Data transfer object containing event creation details.
     * @param role The role of the user making the request.
     * @param request The HTTP request containing authentication details.
     * @return true if the event was successfully created, false otherwise.
     */
    public boolean createEvent(CreateEventDto createEventDto, String role, HttpServletRequest request) {
        if (!checkEvent(createEventDto)) return false;
        if (!hasPermissions(request, createEventDto.getUsername())) return false;
        if (eventTypesService.checkMissing(createEventDto.getType())) return false;
        saveEvent(createEventDto);
        return true;
    }

    /**
     * Edits an existing event.
     * It validates the event data, checks if the user has the necessary permissions, and ensures the event type exists.
     *
     * @param editEventDto Data transfer object containing event editing details.
     * @param role The role of the user making the request.
     * @param request The HTTP request containing authentication details.
     * @return true if the event was successfully edited, false otherwise.
     */
    public boolean editEvent(EditEventDto editEventDto, String role, HttpServletRequest request) {
        if (!checkEditEvent(editEventDto)) return false;
        if (!hasPermissions(request, editEventDto.getUsername())) return false;
        if (eventTypesService.checkMissing(editEventDto.getType())) return false;
        saveEditEvent(editEventDto);
        return true;
    }

    /**
     * Deletes an existing event.
     * It checks if the user has the necessary permissions and ensures the event type exists before deleting.
     *
     * @param editEventDto Data transfer object containing event details for deletion.
     * @param role The role of the user making the request.
     * @param request The HTTP request containing authentication details.
     * @return true if the event was successfully deleted, false otherwise.
     */
    public boolean deleteEvent(EditEventDto editEventDto, String role, HttpServletRequest request) {
        if (!validationUtil.loginCheck(editEventDto.getUsername())) return false;
        if (!hasPermissions(request, editEventDto.getUsername())) return false;
        if (eventTypesService.checkMissing(editEventDto.getType())) return false;
        deleteEvent(editEventDto.getUsername(), editEventDto.getId());
        return true;
    }

    /**
     * Queries all events from the repository and returns them as a response.
     * It filters out deleted events.
     *
     * @return A response containing the list of non-deleted events.
     */
    public EventsResponse queryEvents() {
        List<EventResponseEntity> responseEntities = eventRepository.findAll().stream().filter(eventEntity -> !eventEntity.isDeleted()).map(eventEntity -> new EventResponseEntity(eventEntity.getId(), eventEntity.getTimeStart().toLocalDateTime(), eventEntity.getTimeEnd().toLocalDateTime(), eventEntity.getUser().getUsername(), eventEntity.getType().getName())).collect(Collectors.toList());
        return new EventsResponse(responseEntities);
    }

    /**
     * Checks if the user has the necessary permissions to perform an action on the event.
     *
     * @param request The HTTP request containing authentication details.
     * @param username The username of the user requesting the action.
     * @return true if the user has the necessary permissions, false otherwise.
     */
    private boolean hasPermissions(HttpServletRequest request, String username) {
        Claims c = jwtUtil.extractClaims(securityService.extractCookie(request));
        if (c == null) return false;
        return c.get("role").toString().equals(synchroConfig.getAdminRole()) || c.getSubject().equals(username);
    }

    /**
     * Deletes an event by marking it as deleted in the repository.
     * This operation is asynchronous.
     *
     * @param username The username of the user requesting the deletion.
     * @param id The ID of the event to be deleted.
     */
    @Async
    protected void deleteEvent(String username, Long id) {
        eventRepository.findAllByUser(userRepository.findByUsername(username).get()).forEach(eventEntity -> {
            if (id.equals(eventEntity.getId())) {
                eventRepository.updateEventEntityDeletedById(true, id);
            }
        });
    }

    /**
     * Saves a new event to the repository.
     * This operation is asynchronous.
     *
     * @param createEventDto Data transfer object containing event creation details.
     */
    @Async
    protected void saveEvent(CreateEventDto createEventDto) {
        eventRepository.save(new EventEntity(eventTypeRepository.findEventTypeEntityByName(createEventDto.getType()).get(), userRepository.findByUsername(createEventDto.getUsername()).get(), Timestamp.valueOf(createEventDto.getEnd()), Timestamp.valueOf(createEventDto.getStart())));
    }

    /**
     * Saves the edited event to the repository.
     * This operation is asynchronous.
     *
     * @param editEventDto Data transfer object containing event editing details.
     */
    @Async
    protected void saveEditEvent(EditEventDto editEventDto) {
        eventRepository.updateEventEntityById(userRepository.findByUsername(editEventDto.getUsername()).get(), Timestamp.valueOf(editEventDto.getEnd()), Timestamp.valueOf(editEventDto.getStart()), eventTypeRepository.findEventTypeEntityByName(editEventDto.getType()).get(), editEventDto.getId());
    }

    /**
     * Checks if the event creation data is valid.
     *
     * @param createEventDto Data transfer object containing event creation details.
     * @return true if the event data is valid, false otherwise.
     */
    private boolean checkEvent(CreateEventDto createEventDto) {
        if (!validationUtil.loginCheck(createEventDto.getUsername())) return false;
        return validationUtil.validateEvent(createEventDto);
    }

    /**
     * Checks if the event editing data is valid.
     *
     * @param editEventDto Data transfer object containing event editing details.
     * @return true if the event editing data is valid, false otherwise.
     */
    private boolean checkEditEvent(EditEventDto editEventDto) {
        if (!validationUtil.loginCheck(editEventDto.getUsername())) return false;
        return validationUtil.validateEventEdit(editEventDto);
    }
}
