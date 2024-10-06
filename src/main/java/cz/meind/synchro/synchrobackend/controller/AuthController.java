package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.dto.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.RegisterUserDto;
import cz.meind.synchro.synchrobackend.service.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterUserDto registerUserDto) {
        UserEntity registeredUser = authenticationService.signup(registerUserDto);
        System.out.println(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto, HttpServletResponse response) {
        UserEntity authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        // Create the JWT cookie
        Cookie jwtCookie = new Cookie("token", jwtToken);
        jwtCookie.setHttpOnly(true);  // Prevents JavaScript access for security
        jwtCookie.setSecure(true);    // Ensures the cookie is only sent over HTTPS
        jwtCookie.setPath("/");       // Send this cookie with requests to any path
        jwtCookie.setMaxAge((int) jwtService.getExpirationTime());  // Set expiration

        // Add the cookie to the response
        response.addCookie(jwtCookie);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the JWT token by setting the cookie Max-Age to 0
        Cookie jwtCookie = new Cookie("token", null);
        jwtCookie.setHttpOnly(true);  // Make it HttpOnly for security reasons
        jwtCookie.setSecure(true);    // Ensure it's only sent over HTTPS
        jwtCookie.setPath("/");       // Ensure it applies to all paths
        jwtCookie.setMaxAge(0);       // This will delete the cookie

        // Add the cookie to the response to clear it on the client side
        response.addCookie(jwtCookie);

        return ResponseEntity.ok("Logout successful");
    }
}
