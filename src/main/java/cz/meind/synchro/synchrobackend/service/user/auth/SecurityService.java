package cz.meind.synchro.synchrobackend.service.user.auth;

import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for handling security-related tasks, such as token validation and access control.
 * This class is used for verifying user roles and ensuring that requests are authorized.
 */
@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Constructs an instance of SecurityService with the specified dependencies.
     *
     * @param userRepository Repository for accessing user data.
     * @param jwtUtil Utility for handling JWTs.
     */
    public SecurityService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Filters access based on the provided role by checking the presence and validity of the token in the request.
     * This method extracts the token from the request and validates it against the role.
     *
     * @param request The HTTP request containing the JWT token.
     * @param role The required role for accessing the resource.
     * @return true if the token is present and valid for the specified role, false otherwise.
     */
    public boolean accessFilter(HttpServletRequest request, String role) {
        if (extractCookie(request) == null) return false;
        return attributeAccessFilter(role, extractCookie(request));
    }

    /**
     * Validates the token and checks if the user associated with the token has the required role.
     *
     * @param role The required role for accessing the resource.
     * @param token The JWT token to be validated.
     * @return true if the token is valid and the user has the required role, false otherwise.
     */
    public boolean attributeAccessFilter(String role, String token) {
        try {
            return validateToken(token, role);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Filters access during the signup process, ensuring the user has a valid, unexpired token and the correct role.
     * This method also checks if the user account is enabled and if the role is contained in the token.
     *
     * @param role The required role for accessing the signup process.
     * @param token The JWT token to be validated.
     * @return true if the token is valid for the specified role and the user account is not enabled, false otherwise.
     */
    public boolean signupAttributeAccessFilter(String role, String token) {
        Optional<UserEntity> u = userRepository.findByUsername(jwtUtil.extractClaims(token).getSubject());
        if (u.isEmpty() || u.get().getEnabled()) return false;
        if (!role.contains(jwtUtil.extractClaims(token).get("role").toString())) return false;
        return jwtUtil.isTokenValid(token);
    }

    /**
     * Extracts the JWT token from the cookies of the HTTP request.
     *
     * @param request The HTTP request containing the cookies.
     * @return The value of the "token" cookie, or null if the cookie is not present.
     */
    public String extractCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies())
            if (c.getName().equals("token")) return c.getValue();
        return null;
    }

    /**
     * Validates the provided token and checks if the user associated with the token has the required role.
     * It also checks if the user is enabled and if the token is valid.
     *
     * @param token The JWT token to be validated.
     * @param role The required role for accessing the resource.
     * @return true if the token is valid, the user is enabled, and the user has the required role, false otherwise.
     */
    private boolean validateToken(String token, String role) {
        Optional<UserEntity> u = userRepository.findByUsername(jwtUtil.extractClaims(token).getSubject());
        if (u.isEmpty() || !u.get().getEnabled()) return false;
        if (!role.contains(jwtUtil.extractClaims(token).get("role").toString())) return false;
        return jwtUtil.isTokenValid(token);
    }
}
