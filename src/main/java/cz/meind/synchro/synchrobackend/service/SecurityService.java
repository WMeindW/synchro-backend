package cz.meind.synchro.synchrobackend.service;

import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    public SecurityService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public boolean accessFilter(HttpServletRequest request, String role) {
        Cookie cookie = null;
        if (request.getCookies() == null) return false;
        for (Cookie c : request.getCookies()) {
            System.out.println(c.getName() + "=" + c.getValue());
            if (c.getName().equals("token")) {
                cookie = c;
                break;
            }
        }
        if (cookie == null) return false;
        try {
            return validateToken(cookie.getValue(), role);
        } catch (Exception e) {
            return false;
        }

    }

    public boolean attributeAccessFilter(String role, String token) {
        try {
            return validateToken(token, role);
        } catch (Exception e) {
            return false;
        }

    }

    private boolean validateToken(String token, String role) {
        if (userRepository.findByUsername(jwtUtil.extractClaims(token).getSubject()).isEmpty()) return false;
        if (!role.contains(jwtUtil.extractClaims(token).get("role").toString())) return false;
        return jwtUtil.isTokenValid(token);
    }
}
