package cz.meind.synchro.synchrobackend.controller;


import cz.meind.synchro.synchrobackend.controller.main.Controller;
import cz.meind.synchro.synchrobackend.dto.request.CreateUserDto;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.service.auth.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController extends Controller {

    @Value("${security.jwt.admin-role}")
    private String controllerRole;

    private final AuthenticationService authenticationService;

    public AdminController(SecurityService securityService, AuthenticationService authenticationService) {
        super(securityService);
        this.authenticationService = authenticationService;
    }

    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, controllerRole);
    }
    //Ass kod
    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> createAccount(@RequestBody CreateUserDto createUserDto, HttpServletRequest request, HttpServletResponse response) {
        if (super.handleApiSecureRequest(request, controllerRole)) {
            Optional<LoginResponse> loginResponse = authenticationService.createUser(createUserDto);
            if (loginResponse.isPresent()) {
                return ResponseEntity.ok(loginResponse.get());
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}

