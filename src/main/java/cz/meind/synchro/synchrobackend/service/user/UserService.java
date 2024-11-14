package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.DeleteUserDto;
import cz.meind.synchro.synchrobackend.dto.response.UserListResponse;
import cz.meind.synchro.synchrobackend.dto.response.UserResponseEntity;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final SecurityService securityService;
    private final SynchroConfig synchroConfig;
    private final UserRepository userRepository;

    public UserService(JwtUtil jwtUtil, SecurityService securityService, SynchroConfig synchroConfig, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
        this.synchroConfig = synchroConfig;
        this.userRepository = userRepository;
    }

    public Optional<UserListResponse> queryUserList(HttpServletRequest request) {
        if (!hasPermissions(request)) return Optional.empty();
        return Optional.of(new UserListResponse(userRepository.findAll().stream().map(user -> new UserResponseEntity(user.getId().toString(), user.getUsername(), user.getRole().toString(), user.getEmail(), user.getPhone(), user.getEnabled().toString())).toList()));
    }

    public boolean deleteUser(HttpServletRequest request, DeleteUserDto deleteUserDto) {
        //if (!hasPermissions(request)) return false;

        //TODO: check permissions
        return true;
    }

    private boolean hasPermissions(HttpServletRequest request) {
        Claims c = jwtUtil.extractClaims(securityService.extractCookie(request));
        if (c == null) return false;
        return c.get("role").toString().equals(synchroConfig.getAdminRole());
    }
}
