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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

/**
 * AdminController is a Spring REST controller that provides administrative operations for user and attendance management.
 * It allows for secure user management, including creating, deleting, editing users, managing Motd (Message of the Day),
 * querying user lists, and querying attendance summaries.
 */
@RestController
@RequestMapping("/admin")
public class AdminController extends Controller {

    private final SynchroConfig config;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final InformationService informationService;
    private final AttendanceService attendanceService;

    /**
     * Constructs a new AdminController with the required services.
     *
     * @param securityService       The security service for managing user authentication.
     * @param authenticationService The authentication service for creating users.
     * @param config                The Synchro configuration containing settings.
     * @param userService           The user service for managing user data.
     * @param informationService    The information service for handling messages and summaries.
     * @param attendanceService     The attendance service for managing attendance data.
     */
    public AdminController(SecurityService securityService, AuthenticationService authenticationService, SynchroConfig config, UserService userService, InformationService informationService, AttendanceService attendanceService) {
        super(securityService);
        this.authenticationService = authenticationService;
        this.config = config;
        this.userService = userService;
        this.informationService = informationService;
        this.attendanceService = attendanceService;
    }

    /**
     * Handles the request for the admin index page and redirects to the appropriate secure page.
     *
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A ResponseEntity with the appropriate status and content.
     */
    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, config.getAdminRole());
    }

    /**
     * Creates a new user based on the provided CreateUserDto.
     *
     * @param createUserDto The data transfer object containing the new user information.
     * @param request       The HTTP servlet request.
     * @return A ResponseEntity with the result of the operation.
     */
    @PostMapping(value = "/create-user", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDto createUserDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<LoginResponse> loginResponse = authenticationService.createUser(createUserDto);
        if (loginResponse.isPresent()) return ResponseEntity.ok(loginResponse.get());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Deletes a user based on the provided DeleteUserDto.
     *
     * @param deleteUserDto The data transfer object containing the user to be deleted.
     * @param request       The HTTP servlet request.
     * @return A ResponseEntity indicating the result of the deletion operation.
     */
    @PostMapping(value = "/delete-user", produces = "application/json")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserDto deleteUserDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userService.deleteUser(deleteUserDto)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Queries the list of users.
     *
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the list of users or a BAD_REQUEST status.
     */
    @GetMapping(value = "/query-user", produces = "application/json")
    public ResponseEntity<?> queryUser(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<UserListResponse> responses = userService.queryUserList();
        if (responses.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(responses.get());
    }

    /**
     * Edits a user based on the provided EditUserDto.
     *
     * @param editUserDto The data transfer object containing the user edits.
     * @param request     The HTTP servlet request.
     * @return A ResponseEntity indicating the result of the edit operation.
     */
    @PostMapping(value = "/edit-user", produces = "application/json")
    public ResponseEntity<?> editUser(@RequestBody EditUserDto editUserDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userService.editUser(editUserDto)) return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Saves the Message of the Day (Motd) provided in the request.
     *
     * @param motdDto The data transfer object containing the message.
     * @param request The HTTP servlet request.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PostMapping(value = "/save-motd", produces = "application/json")
    public ResponseEntity<?> saveMotd(@RequestBody MotdDto motdDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        informationService.saveMotd(motdDto.getMotd());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Tests the Message of the Day (Motd) provided in the request and returns the result.
     *
     * @param motdDto The data transfer object containing the message to test.
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the result of the test.
     */
    @PostMapping(value = "/test-motd", produces = "text/html")
    public ResponseEntity<?> testMotd(@RequestBody MotdDto motdDto, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(informationService.testMotd(motdDto.getMotd()));
    }

    /**
     * Queries the attendance information.
     *
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the attendance information.
     */
    @GetMapping(value = "/query-attendance", produces = "application/json")
    public ResponseEntity<?> queryAttendance(HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(attendanceService.queryAttendance());
    }

    /**
     * Queries the summary for the specified month.
     *
     * @param month   The month for which the summary is requested.
     * @param request The HTTP servlet request.
     * @return A ResponseEntity containing the summary data.
     */
    @GetMapping(value = "/query-summary", produces = "application/json")
    public ResponseEntity<?> querySummary(@RequestParam LocalDate month, HttpServletRequest request) {
        if (!super.handleApiSecureRequest(request, config.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(informationService.querySummary(month));
    }
}


