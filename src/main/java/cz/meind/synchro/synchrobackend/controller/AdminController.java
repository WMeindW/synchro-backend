package cz.meind.synchro.synchrobackend.controller;


import cz.meind.synchro.synchrobackend.dto.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.RegisterUserDto;
import cz.meind.synchro.synchrobackend.service.AuthenticationService;
import cz.meind.synchro.synchrobackend.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Value("${security.jwt.secure-route}")
    private String secureRoute;

    @Value("${security.jwt.admin-role}")
    private String controllerRole;

    private final SecurityService securityService;

    public AdminController(SecurityService securityService) {
        this.securityService = securityService;
    }
/*
    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        if (!securityService.accessFilter(request, controllerRole)) {
            try {
                response.sendRedirect("/login.html");
            } catch (IOException e) {
                return ResponseEntity.status(404).body("File not found: Redirect Failed");
            }
            return ResponseEntity.status(403).body("Access Denied");
        }
        try {
            return ResponseEntity.ok(Files.readAllBytes(Path.of(secureRoute + request.getRequestURI())));
        } catch (IOException e) {
            return ResponseEntity.status(404).body("File not found");
        }
    }

 */
}

