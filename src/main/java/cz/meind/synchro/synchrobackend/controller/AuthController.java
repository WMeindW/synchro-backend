package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.request.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.request.RegisterUserDto;
import cz.meind.synchro.synchrobackend.service.user.auth.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController extends Controller {

    private final AuthenticationService authService;
    private final SynchroConfig synchroConfig;


    public AuthController(AuthenticationService authService, SecurityService securityService, SynchroConfig synchroConfig) {
        super(securityService);
        this.authService = authService;
        this.synchroConfig = synchroConfig;
    }

    @GetMapping(value = "/login.html", produces = "text/html")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsUnsecureRedirect(request, response);
    }

    @GetMapping(value = "/signup.html", produces = "text/html")
    public ResponseEntity<?> register(@RequestParam String token, HttpServletRequest request, HttpServletResponse response) {
        return super.permitSignUp(request, response, token);
    }

    @GetMapping(value = "/auth-styles.css", produces = "text/css")
    public ResponseEntity<?> style(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsUnsecureRedirect(request, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) {
        if (authService.signup(registerUserDto)) return ResponseEntity.ok(registerUserDto);
        else return ResponseEntity.status(409).body("Error occurred while registering.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto request, HttpServletResponse response) {
        Optional<LoginResponse> login = authService.login(request);
        if (login.isEmpty()) return ResponseEntity.status(409).body("Error occurred while logging in.");
        response.addCookie(super.setCookie("token", login.get().getToken(), login.get().getExpiresIn(), true));
        response.addCookie(super.setCookie("username", login.get().getUsername(), login.get().getExpiresIn(), false));
        response.addCookie(super.setCookie("role", login.get().getRole(), login.get().getExpiresIn(), false));
        return ResponseEntity.ok(login.get());
    }

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
