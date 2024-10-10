package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.request.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.request.RegisterUserDto;
import cz.meind.synchro.synchrobackend.service.auth.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController extends Controller {

    private final AuthenticationService authService;


    public AuthController(AuthenticationService authService, SecurityService securityService) {
        super(securityService);
        this.authService = authService;
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
        response.addCookie(super.setCookie("token", login.get().getToken(), login.get().getExpiresIn()));
        return ResponseEntity.ok(login.get());

    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addCookie(super.setCookie("token", null, 0));
        return ResponseEntity.ok("Logout successful");
    }
}
