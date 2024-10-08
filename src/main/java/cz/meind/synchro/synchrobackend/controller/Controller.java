package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class Controller {
    @Value("${security.jwt.secure-route}")
    private String secureRoute;

    private final SecurityService securityService;

    public Controller(SecurityService securityService) {
        this.securityService = securityService;
    }

    protected ResponseEntity<?> handleRequests(HttpServletRequest request, HttpServletResponse response, String role){
        if (!securityService.accessFilter(request, role)) {
            try {
                response.sendRedirect("/login.html");
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            return ResponseEntity.ok(Files.readAllBytes(Path.of(secureRoute + request.getRequestURI())));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
