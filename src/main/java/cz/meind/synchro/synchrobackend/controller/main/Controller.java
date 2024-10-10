package cz.meind.synchro.synchrobackend.controller.main;

import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
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

    @Value("${security.jwt.combined-role}")
    private String combinedRole;

    private final SecurityService securityService;

    public Controller(SecurityService securityService) {
        this.securityService = securityService;
    }

    protected ResponseEntity<?> handleRequestsSecureRedirect(HttpServletRequest request, HttpServletResponse response, String role) {
        if (!securityService.accessFilter(request, role)) {
            try {
                response.sendRedirect("/synchro/api/auth/login.html");
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

    protected ResponseEntity<?> handleRequestsUnsecureRedirect(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(secureRoute + request.getRequestURI());
        if (securityService.accessFilter(request, combinedRole)) {
            try {
                response.sendRedirect("/synchro/api/user/index.html");
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
        try {
            return ResponseEntity.ok(Files.readAllBytes(Path.of(secureRoute + request.getRequestURI())));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    protected Boolean handleApiSecureRequest(HttpServletRequest request, String role) {
        return securityService.accessFilter(request, role);
    }

    protected ResponseEntity<?> permitSignUp(HttpServletRequest request, HttpServletResponse response, String token) {
        System.out.println(secureRoute + request.getRequestURI());
        if (securityService.accessFilter(request, combinedRole)) {
            try {
                response.sendRedirect("/synchro/api/user/index.html");
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
        if (securityService.attributeAccessFilter(combinedRole, token)) {
            try {
                return ResponseEntity.ok(Files.readAllBytes(Path.of(secureRoute + request.getRequestURI())));
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            try {
                response.sendRedirect("/synchro/api/auth/login.html");
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
    }
}
