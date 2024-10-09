package cz.meind.synchro.synchrobackend.controller;


import cz.meind.synchro.synchrobackend.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin")
public class AdminController extends Controller {

    @Value("${security.jwt.admin-role}")
    private String controllerRole;

    public AdminController(SecurityService securityService) {
        super(securityService);
    }

    @GetMapping(value = "/index.html", produces = "text/html")
    public ResponseEntity<?> index(HttpServletRequest request, HttpServletResponse response) {
        return super.handleRequestsSecureRedirect(request, response, controllerRole);
    }


}

