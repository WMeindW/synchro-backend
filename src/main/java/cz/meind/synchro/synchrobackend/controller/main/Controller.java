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

/**
 * Controller is a base class that provides utility methods for handling secure and unsecure redirects,
 * managing cookies, and handling user access control. It is used as a parent class for other controllers.
 */
@Component
public class Controller {

    private SynchroConfig config;
    private final SecurityService securityService;
    private Routes router;

    /**
     * Sets the Routes object used for redirecting and retrieving files based on the request.
     *
     * @param router The Routes object.
     */
    @Autowired
    public void setRouter(Routes router) {
        this.router = router;
    }

    /**
     * Sets the Synchro configuration containing application settings.
     *
     * @param config The Synchro configuration.
     */
    @Autowired
    public void setConfig(SynchroConfig config) {
        this.config = config;
    }

    /**
     * Constructs a new Controller with the specified SecurityService.
     *
     * @param securityService The security service responsible for user authentication and authorization.
     */
    public Controller(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Handles secure requests and redirects the user to the login page if access is forbidden.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param role The required role for accessing the requested resource.
     * @return A ResponseEntity containing the response for the secure redirect.
     */
    protected ResponseEntity<?> handleRequestsSecureRedirect(HttpServletRequest request, HttpServletResponse response, String role) {
        if (this.accessFilterRedirectLogin(request, response, role)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return ResponseEntity.ok(router.getFile(request));
    }

    /**
     * Handles unsecure requests and redirects the user to the dashboard if they are already logged in.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @return A ResponseEntity containing the response for the unsecure redirect.
     */
    protected ResponseEntity<?> handleRequestsUnsecureRedirect(HttpServletRequest request, HttpServletResponse response) {
        if (this.accessFilterRedirectDashboard(request, response, config.getCombinedRole())) return new ResponseEntity<>(HttpStatus.FOUND);
        return ResponseEntity.ok(router.getFile(request));
    }

    /**
     * Checks if the API request is secure by verifying the user's access rights.
     *
     * @param request The HTTP servlet request.
     * @param role The required role for accessing the requested resource.
     * @return A Boolean indicating whether the user has the necessary access rights.
     */
    protected Boolean handleApiSecureRequest(HttpServletRequest request, String role) {
        return securityService.accessFilter(request, role);
    }

    /**
     * Handles the signup process and checks if the user can access the signup page.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param token The signup token.
     * @return A ResponseEntity containing the response for the signup process.
     */
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

    /**
     * Creates and configures a new Cookie object with the specified parameters.
     *
     * @param name The name of the cookie.
     * @param value The value to be stored in the cookie.
     * @param expiration The expiration time of the cookie in seconds.
     * @param httpOnly If true, the cookie will be marked as HTTP-only.
     * @return A new Cookie object configured with the given parameters.
     */
    protected Cookie setCookie(String name, String value, long expiration, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge((int) expiration);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * Redirects the user to the dashboard if they have the appropriate access.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param role The required role for accessing the dashboard.
     * @return A Boolean indicating whether the user was redirected to the dashboard.
     */
    private boolean accessFilterRedirectDashboard(HttpServletRequest request, HttpServletResponse response, String role) {
        if (securityService.accessFilter(request, role)) {
            router.redirect(response, config.getUserDashboardPage());
            return true;
        }
        return false;
    }

    /**
     * Redirects the user to the login page if they do not have the appropriate access.
     *
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param role The required role for accessing the requested resource.
     * @return A Boolean indicating whether the user was redirected to the login page.
     */
    private boolean accessFilterRedirectLogin(HttpServletRequest request, HttpServletResponse response, String role) {
        if (!securityService.accessFilter(request, role)) {
            router.redirect(response, config.getLoginPage());
            return true;
        }
        return false;
    }
}

