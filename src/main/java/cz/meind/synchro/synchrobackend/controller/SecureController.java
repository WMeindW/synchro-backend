package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
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

    public SecureController(SecurityService securityService, SynchroConfig config, ScheduleService scheduleService) {
        super(securityService);
        this.config = config;
        this.scheduleService = scheduleService;
    }

    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, config.getCombinedRole());
    }

    @PostMapping(value = "/create-event", produces = "text/html")
    @CrossOrigin
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDto createEventDto, HttpServletRequest request) {
        //if (!super.handleApiSecureRequest(request, config.getCombinedRole())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!scheduleService.createEvent(createEventDto, config.getCombinedRole(), request)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok("Success");
    }

    @GetMapping(value = "/query-event", produces = "text/html")
    public ResponseEntity<?> queryEvent(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok("Success");
    }
}
