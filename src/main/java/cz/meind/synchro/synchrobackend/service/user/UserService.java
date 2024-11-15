package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.CheckRepository;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.DeleteUserDto;
import cz.meind.synchro.synchrobackend.dto.request.EditUserDto;
import cz.meind.synchro.synchrobackend.dto.response.UserListResponse;
import cz.meind.synchro.synchrobackend.dto.response.UserResponseEntity;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final SecurityService securityService;
    private final SynchroConfig synchroConfig;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CheckRepository checkRepository;
    private final ValidationUtil validationUtil;
    private final RoleRepository roleRepository;

    public UserService(JwtUtil jwtUtil, SecurityService securityService, SynchroConfig synchroConfig, UserRepository userRepository, EventRepository eventRepository, CheckRepository checkRepository, ValidationUtil validationUtil, RoleRepository roleRepository) {
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
        this.synchroConfig = synchroConfig;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.checkRepository = checkRepository;
        this.validationUtil = validationUtil;
        this.roleRepository = roleRepository;
    }

    public Optional<UserListResponse> queryUserList(HttpServletRequest request) {
        // if (!hasPermissions(request)) return Optional.empty();
        return Optional.of(new UserListResponse(userRepository.findAll().stream().map(user -> new UserResponseEntity(user.getId().toString(), user.getUsername(), user.getRole().toString(), user.getEmail(), user.getPhone(), user.getEnabled().toString())).toList()));
    }

    public boolean deleteUser(HttpServletRequest request, DeleteUserDto deleteUserDto) {
        //if (!hasPermissions(request)) return false;
        if (deleteUserDto.getUsername().equals(synchroConfig.getAdminUsername())) return false;
        Optional<UserEntity> u = userRepository.findByUsername(deleteUserDto.getUsername());
        if (u.isEmpty() || !u.get().getId().toString().equals(deleteUserDto.getId())) return false;
        updateSetEnabled(u.get());
        return true;
    }

    public boolean editUser(HttpServletRequest request, EditUserDto editUserDto) {
        //if (!hasPermissions(request)) return false;
        if (!validationUtil.editCheck(editUserDto)) return false;
        updateUser(editUserDto);
        return true;
    }

    @Async
    protected void updateSetEnabled(UserEntity u) {
        if (u.getEnabled()) userRepository.updateUserEnabledByUsername(u.getUsername(), false);
        else updateDelete(u);
    }

    @Async
    protected void updateUser(EditUserDto u) {
        userRepository.updateUserEntityById(u.getId(), u.getUsername(), validationUtil.hashPassword(u.getPassword()), roleRepository.findRoleEntityByName(u.getRole()).get(), u.getEmail(), u.getPhone());
    }

    @Async
    protected void updateDelete(UserEntity u) {
        eventRepository.deleteAll(u.getEvents());
        checkRepository.deleteAll(u.getChecks());
        userRepository.delete(u);
    }

    private boolean hasPermissions(HttpServletRequest request) {
        Claims c = jwtUtil.extractClaims(securityService.extractCookie(request));
        if (c == null) return false;
        return c.get("role").toString().equals(synchroConfig.getAdminRole());
    }
}
