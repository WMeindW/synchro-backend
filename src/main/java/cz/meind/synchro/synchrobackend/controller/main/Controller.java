package cz.meind.synchro.synchrobackend.controller.main;

import cz.meind.synchro.synchrobackend.config.Routes;
import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

    protected ResponseEntity<?> handleRequestsSecureRedirect(HttpServletRequest request, HttpServletResponse response, String role) {
        if (this.accessFilterRedirectLogin(request, response, role)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(router.getFile(request));
    }

    protected ResponseEntity<?> handleRequestsUnsecureRedirect(HttpServletRequest request, HttpServletResponse response) {
        if (this.accessFilterRedirectDashboard(request, response, config.getCombinedRole())) return new ResponseEntity<>(HttpStatus.FOUND);
        return ResponseEntity.ok(router.getFile(request));
    }

    protected Boolean handleApiSecureRequest(HttpServletRequest request, String role) {
        return securityService.accessFilter(request, role);
    }

    protected ResponseEntity<?> permitSignUp(HttpServletRequest request, HttpServletResponse response, String token) {
        if (this.accessFilterRedirectDashboard(request, response, config.getCombinedRole()))
            return new ResponseEntity<>(HttpStatus.FOUND);
        if (securityService.signupAttributeAccessFilter(config.getCombinedRole(), token)){
            return ResponseEntity.ok(router.getFile(request));
        }
        System.out.println("Redirecting");
        router.redirect(response, config.getLoginPage());
        return new ResponseEntity<>(HttpStatus.FOUND);
    }

    protected Cookie setCookie(String name,String value, long expiration, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge((int) expiration);
        cookie.setPath("/");
        return cookie;
    }

    private boolean accessFilterRedirectDashboard(HttpServletRequest request, HttpServletResponse response, String role) {
        if (securityService.accessFilter(request, role)) {
            router.redirect(response, config.getUserDashboardPage());
            return true;
        }
        return false;
    }

    private boolean accessFilterRedirectLogin(HttpServletRequest request, HttpServletResponse response, String role) {
        if (!securityService.accessFilter(request, role)) {
            router.redirect(response, config.getLoginPage());
            return true;
        }
        return false;
    }
}
