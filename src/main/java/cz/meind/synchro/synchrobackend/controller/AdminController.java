package cz.meind.synchro.synchrobackend.controller;


import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.request.CreateUserDto;
import cz.meind.synchro.synchrobackend.dto.request.DeleteUserDto;
import cz.meind.synchro.synchrobackend.dto.request.EditUserDto;
import cz.meind.synchro.synchrobackend.dto.request.MotdDto;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.response.UserListResponse;
import cz.meind.synchro.synchrobackend.service.user.AttendanceService;
import cz.meind.synchro.synchrobackend.service.user.InformationService;
import cz.meind.synchro.synchrobackend.service.user.UserService;
import cz.meind.synchro.synchrobackend.service.user.auth.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController extends Controller {

    private final SynchroConfig config;

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final InformationService informationService;
    private final AttendanceService attendanceService;

    public AdminController(SecurityService securityService, AuthenticationService authenticationService, SynchroConfig config, UserService userService, InformationService informationService, AttendanceService attendanceService) {
        super(securityService);
        this.authenticationService = authenticationService;
        this.config = config;
        this.userService = userService;
        this.informationService = informationService;
        this.attendanceService = attendanceService;
    }

    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, config.getAdminRole());
    }

    @PostMapping(value = "/create-user", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDto createUserDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<LoginResponse> loginResponse = authenticationService.createUser(createUserDto);
        if (loginResponse.isPresent()) return ResponseEntity.ok(loginResponse.get());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/delete-user", produces = "application/json")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserDto deleteUserDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (userService.deleteUser(deleteUserDto)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @GetMapping(value = "/query-user", produces = "application/json")
    public ResponseEntity<?> queryUser(HttpServletRequest request) {
        /*if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

         */
        Optional<UserListResponse> responses = userService.queryUserList();
        if (responses.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(responses.get());
    }

    @PostMapping(value = "/edit-user", produces = "application/json")
    public ResponseEntity<?> editUser(@RequestBody EditUserDto editUserDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (userService.editUser(editUserDto)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/save-motd", produces = "application/json")
    public ResponseEntity<?> saveMotd(@RequestBody MotdDto motdDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        informationService.saveMotd(motdDto.getMotd());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/test-motd", produces = "text/html")
    public ResponseEntity<?> testMotd(@RequestBody MotdDto motdDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(informationService.testMotd(motdDto.getMotd()));
    }

    @GetMapping(value = "/query-attendance", produces = "application/json")
    public ResponseEntity<?> queryAttendance(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(attendanceService.queryAttendance());
    }

    @CrossOrigin
    @GetMapping(value = "/query-summary", produces = "application/json")
    public ResponseEntity<?> querySummary(@RequestParam LocalDate month, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(informationService.querySummary(month));
    }
}

