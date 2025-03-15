package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditEventDto;
import cz.meind.synchro.synchrobackend.service.events.ScheduleService;
import cz.meind.synchro.synchrobackend.service.user.AttendanceService;
import cz.meind.synchro.synchrobackend.service.user.InformationService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * SecureController is a controller responsible for handling user-specific requests related to events,
 * attendance, and information in a secure manner. It ensures that only authorized users can access certain resources.
 */
@RestController
@RequestMapping("/user")
public class SecureController extends Controller {

    private final SynchroConfig config;
    private final ScheduleService scheduleService;
    private final AttendanceService attendanceService;
    private final InformationService informationService;

    /**
     * Constructs a new SecureController with the specified services and configuration.
     *
     * @param securityService    The security service responsible for user authentication and authorization.
     * @param config             The configuration containing application settings.
     * @param scheduleService    The service responsible for managing events and schedules.
     * @param attendanceService  The service responsible for managing user attendance.
     * @param informationService The service responsible for handling information like MOTD.
     */
    public SecureController(SecurityService securityService, SynchroConfig config, ScheduleService scheduleService, AttendanceService attendanceService, InformationService informationService) {
        super(securityService);
        this.config = config;
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.informationService = informationService;
    }

    /**
     * Handles the request to access the user index page with secure redirects.
     *
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A ResponseEntity containing the response for the secure redirect.
     */
    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, config.getCombinedRole());
    }

    /**
     * Handles the request to create a new event if the user has the appropriate access rights.
     *
     * @param createEventDto The data transfer object containing the event details.
     * @param request        The HTTP servlet request.
     * @return A ResponseEntity containing the response for the create event operation.
     */
    @PostMapping(value = "/create-event", produces = "text/html")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDto createEventDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.createEvent(createEventDto, config.getCombinedRole(), request))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Handles the request to edit an existing event if the user has the appropriate access rights.
     *
     * @param editEventDto The data transfer object containing the event details to be edited.
     * @param request      The HTTP servlet request.
     * @return A ResponseEntity containing the response for the edit event operation.
     */
    @PostMapping(value = "/edit-event", produces = "text/html")
    public ResponseEntity<?> editEvent(@RequestBody EditEventDto editEventDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.editEvent(editEventDto, config.getCombinedRole(), request))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Handles the request to delete an event if the user has the appropriate access rights.
     *
     * @param editEventDto The data transfer object containing the event details to be deleted.
     * @param request      The HTTP servlet request.
     * @return A ResponseEntity containing the response for the delete event operation.
     */
    @PostMapping(value = "/delete-event", produces = "text/html")
    public ResponseEntity<?> deleteEvent(@RequestBody EditEventDto editEventDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.deleteEvent(editEventDto, config.getCombinedRole(), request))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Handles the request to query events, returning the list of events.
     *
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the list of events.
     */
    @GetMapping(value = "/query-event", produces = "application/json")
    public ResponseEntity<?> queryEvent(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(scheduleService.queryEvents());
    }

    /**
     * Handles the request to query the attendance status of a user by their username.
     *
     * @param username The username of the user whose attendance is being queried.
     * @param request  The HTTP servlet request.
     * @return A ResponseEntity containing the attendance status of the user.
     */
    @GetMapping(value = "/query-attendance", produces = "application/json")
    public ResponseEntity<?> queryAttendance(@RequestParam String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(attendanceService.isCheckedIn(request, username).toString());
    }

    /**
     * Handles the request to check the attendance of a user by their username.
     *
     * @param username The username of the user whose attendance is being checked.
     * @param request  The HTTP servlet request.
     * @return A ResponseEntity indicating the success or failure of the attendance check operation.
     */
    @PostMapping(value = "/check-attendance", produces = "text/html")
    public ResponseEntity<?> checkAttendance(@RequestBody String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!attendanceService.checkUser(request, username)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Handles the request to query the Message of the Day (MOTD).
     *
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the MOTD.
     */
    @GetMapping(value = "/query-motd", produces = "text/html")
    public ResponseEntity<?> queryMotd(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(informationService.queryMotd());
    }

    /**
     * Handles the request to query general information related to the user.
     *
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the general information for the user.
     */
    @GetMapping(value = "/query-info", produces = "application/json")
    public ResponseEntity<?> queryInfo(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(informationService.queryInfo());
    }
}
