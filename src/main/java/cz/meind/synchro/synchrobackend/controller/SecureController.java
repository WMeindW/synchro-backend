package cz.meind.synchro.synchrobackend.controller;

import cz.meind.synchro.synchrobackend.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class SecureController extends Controller {

    @Value("${security.jwt.combined-role}")
    private String controllerRole;


    public SecureController(SecurityService securityService) {
        super(securityService);
    }

    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequests(request, response, controllerRole);
    }
}
