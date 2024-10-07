package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.dto.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.RegisterUserDto;
import cz.meind.synchro.synchrobackend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authService;


    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto request) {
        if (authService.signup(request)) return ResponseEntity.ok(request);
        else return ResponseEntity.status(409).body("Error occurred while registering.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto request, HttpServletResponse response) {// The token is in the "token" field
        Optional<LoginResponse> login = authService.login(request);
        if (login.isEmpty()) return ResponseEntity.status(409).body("Error occurred while logging in.");
        else {
            Cookie cookie = new Cookie("jwt", login.get().getToken());
            cookie.setHttpOnly(true);
            cookie.setMaxAge((int) login.get().getExpiresIn()); // Expire the cookie
            cookie.setPath("/");
            // Add the expired cookie to the response
            response.addCookie(cookie);
            return ResponseEntity.ok(login.get());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Create an expired cookie to effectively "logout" the user
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expire the cookie
        cookie.setPath("/");

        // Add the expired cookie to the response
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout successful");
    }
}
