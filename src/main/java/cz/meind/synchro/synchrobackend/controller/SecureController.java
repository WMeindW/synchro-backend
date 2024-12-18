package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditEventDto;
import cz.meind.synchro.synchrobackend.service.user.AttendanceService;
import cz.meind.synchro.synchrobackend.service.user.InformationService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.events.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class SecureController extends Controller {


    private final SynchroConfig config;
    private final ScheduleService scheduleService;
    private final AttendanceService attendanceService;
    private final InformationService informationService;

    public SecureController(SecurityService securityService, SynchroConfig config, ScheduleService scheduleService, AttendanceService attendanceService, InformationService informationService) {
        super(securityService);
        this.config = config;
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.informationService = informationService;
    }

    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, config.getCombinedRole());
    }

    @PostMapping(value = "/create-event", produces = "text/html")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDto createEventDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.createEvent(createEventDto, config.getCombinedRole(), request))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/edit-event", produces = "text/html")
    public ResponseEntity<?> editEvent(@RequestBody EditEventDto editEventDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.editEvent(editEventDto, config.getCombinedRole(), request))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/delete-event", produces = "text/html")
    public ResponseEntity<?> deleteEvent(@RequestBody EditEventDto editEventDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.deleteEvent(editEventDto, config.getCombinedRole(), request))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/query-event", produces = "application/json")
    public ResponseEntity<?> queryEvent(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(scheduleService.queryEvents());
    }

    @GetMapping(value = "/query-attendance", produces = "application/json")
    public ResponseEntity<?> queryAttendance(@RequestParam String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(attendanceService.isCheckedIn(request, username).toString());
    }

    @PostMapping(value = "/check-attendance", produces = "text/html")
    public ResponseEntity<?> checkAttendance(@RequestBody String username, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!attendanceService.checkUser(request, username)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/query-motd", produces = "text/html")
    public ResponseEntity<?> queryMotd(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(informationService.queryMotd());
    }

    @CrossOrigin
    @GetMapping(value = "/query-info", produces = "application/json")
    public ResponseEntity<?> queryInfo(HttpServletRequest request) {
        //if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
           // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(informationService.queryInfo());
    }

}
