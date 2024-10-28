package cz.meind.synchro.synchrobackend.service.auth;

import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    public SecurityService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public boolean accessFilter(HttpServletRequest request, String role) {
        if (extractCookie(request) == null) return false;
        return attributeAccessFilter(role, extractCookie(request));
    }

    public boolean attributeAccessFilter(String role, String token) {
        try {
            return validateToken(token, role);
        } catch (Exception e) {
            return false;
        }

    }

    public String extractCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies())
            if (c.getName().equals("token")) return c.getValue();
        return null;
    }

    private boolean validateToken(String token, String role) {
        if (userRepository.findByUsername(jwtUtil.extractClaims(token).getSubject()).isEmpty()) return false;
        if (!role.contains(jwtUtil.extractClaims(token).get("role").toString())) return false;
        return jwtUtil.isTokenValid(token);
    }
}
