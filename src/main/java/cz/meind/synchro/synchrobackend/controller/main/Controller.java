package cz.meind.synchro.synchrobackend.controller.main;

import cz.meind.synchro.synchrobackend.config.Routes;
import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.service.auth.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class Controller {

    private SynchroConfig config;
    private final SecurityService securityService;
    private Routes router;

    @Autowired
    public void setRouter(Routes router) {
        this.router = router;
    }

    @Autowired
    public void setConfig(SynchroConfig config) {
        this.config = config;
    }

    public Controller(SecurityService securityService) {
        this.securityService = securityService;
    }

    //Ass kod
    protected ResponseEntity<?> handleRequestsSecureRedirect(HttpServletRequest request, HttpServletResponse response, String role) {
        if (!securityService.accessFilter(request, role)) {
            router.redirect(response, config.getLoginPage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(router.getFile(request));
    }

    protected ResponseEntity<?> handleRequestsUnsecureRedirect(HttpServletRequest request, HttpServletResponse response) {
        if (securityService.accessFilter(request, config.getCombinedRole())) {
            router.redirect(response, config.getUserDashboardPage());
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
        return ResponseEntity.ok(router.getFile(request));
    }

    protected Boolean handleApiSecureRequest(HttpServletRequest request, String role) {
        return securityService.accessFilter(request, role);
    }

    protected ResponseEntity<?> permitSignUp(HttpServletRequest request, HttpServletResponse response, String token) {
        if (securityService.accessFilter(request, config.getCombinedRole())) {
            router.redirect(response, config.getUserDashboardPage());
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
        if (securityService.attributeAccessFilter(config.getCombinedRole(), token)) {
            return ResponseEntity.ok(router.getFile(request));
        } else {
            router.redirect(response, config.getLoginPage());
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
    }

    protected Cookie setCookie(String value, long expiration) {
        Cookie cookie = new Cookie("token", value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) expiration);
        cookie.setPath("/");
        return cookie;
    }
}
