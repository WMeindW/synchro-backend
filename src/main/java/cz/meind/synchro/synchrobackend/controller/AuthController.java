package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.request.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.request.RegisterUserDto;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.service.user.auth.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * AuthController is a Spring REST controller that handles authentication-related operations such as login, signup, and logout.
 * It allows for secure user registration, login, and session management using cookies.
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends Controller {

    private final AuthenticationService authService;
    private final SynchroConfig synchroConfig;

    /**
     * Constructs a new AuthController with the required services.
     *
     * @param authService     The authentication service for managing user authentication.
     * @param securityService The security service for managing user security and session.
     * @param synchroConfig   The Synchro configuration containing settings.
     */
    public AuthController(AuthenticationService authService, SecurityService securityService, SynchroConfig synchroConfig) {
        super(securityService);
        this.authService = authService;
        this.synchroConfig = synchroConfig;
    }

    /**
     * Handles the request for the login page and redirects to the appropriate unsecured page.
     *
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A ResponseEntity with the appropriate status and content.
     */
    @GetMapping(value = "/login.html", produces = "text/html")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsUnsecureRedirect(request, response);
    }

    /**
     * Handles the request for the signup page with a token.
     *
     * @param token    The token used for user registration.
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A ResponseEntity with the appropriate status and content.
     */
    @GetMapping(value = "/signup.html", produces = "text/html")
    public ResponseEntity<?> register(@RequestParam String token, HttpServletRequest request, HttpServletResponse response) {
        return super.permitSignUp(request, response, token);
    }

    /**
     * Handles the request for the stylesheet used in the authentication pages.
     *
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A ResponseEntity with the appropriate status and content.
     */
    @GetMapping(value = "/auth-styles.css", produces = "text/css")
    public ResponseEntity<?> style(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsUnsecureRedirect(request, response);
    }

    /**
     * Handles the registration of a new user based on the provided RegisterUserDto.
     *
     * @param registerUserDto The data transfer object containing the new user registration information.
     * @return A ResponseEntity indicating the result of the registration operation.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        if (authService.signup(registerUserDto)) return ResponseEntity.ok(registerUserDto);
        else return ResponseEntity.status(409).body("Error occurred while registering.");
    }

    /**
     * Handles the login process for a user based on the provided LoginUserDto.
     *
     * @param request  The login credentials of the user.
     * @param response The HTTP servlet response used to add cookies.
     * @return A ResponseEntity containing the login response or an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto request, HttpServletResponse response) {
        Optional<LoginResponse> login = authService.login(request);
        if (login.isEmpty()) return ResponseEntity.status(409).body("Error occurred while logging in.");
        response.addCookie(super.setCookie("token", login.get().getToken(), login.get().getExpiresIn(), true));
        response.addCookie(super.setCookie("username", login.get().getUsername(), login.get().getExpiresIn(), false));
        response.addCookie(super.setCookie("role", login.get().getRole(), login.get().getExpiresIn(), false));
        return ResponseEntity.ok(login.get());
    }

    /**
     * Handles the logout process, invalidates the user's session, and clears cookies.
     *
     * @param request  The HTTP servlet request.
     * @param response The HTTP servlet response used to clear the cookies.
     * @return A ResponseEntity indicating the result of the logout operation.
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        if (!super.handleApiSecureRequest(request, synchroConfig.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        authService.logout(request);
        response.addCookie(super.setCookie("token", null, 0, true));
        response.addCookie(super.setCookie("username", null, 0, false));
        return ResponseEntity.ok("Logout successful");
    }
}
