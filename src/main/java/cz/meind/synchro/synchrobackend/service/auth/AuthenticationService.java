package cz.meind.synchro.synchrobackend.service.auth;

import cz.meind.synchro.synchrobackend.database.entities.RoleEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateUserDto;
import cz.meind.synchro.synchrobackend.dto.request.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.request.RegisterUserDto;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthenticationService {
    //Ass kod
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtUtil jwtUtil;

    private final ValidationUtil validationUtil;

    @Value("${security.jwt.default-role}")
    private String defaultRole;

    @Value("${security.jwt.host-address}")
    private String host;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    @Value("${security.jwt.admin-username:admin_user}")
    private String adminUsername;

    @Value("${security.jwt.admin-password}")
    private String adminPassword;

    @Value("${security.jwt.signup-link-expires}")
    private long signupLinkExpires;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil, ValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.validationUtil = validationUtil;
    }

    @PostConstruct
    private void initializeAdmin() {
        if (userRepository.findByUsername(adminUsername).isPresent()) return;
        if (roleRepository.findRoleEntityByName("ADMIN").isPresent()) return;
        roleRepository.save(new RoleEntity("ADMIN"));
        userRepository.save(new UserEntity(adminUsername, validationUtil.hashPassword(adminPassword), true, roleRepository.findRoleEntityByName("ADMIN").get()));

    }

    @PostConstruct
    private void initializeUser() {
        if (roleRepository.findRoleEntityByName(defaultRole).isEmpty())
            roleRepository.save(new RoleEntity(defaultRole));
    }

    public boolean signup(RegisterUserDto registerUserDto) {
        if (!validationUtil.signupCheck(registerUserDto.getUsername())) return false;
        if (!registerUserDto.getUsername().equals(jwtUtil.extractClaims(registerUserDto.getToken()).getSubject()))
            return false;
        userRepository.updateUserEnabledAndPasswordByUsername(registerUserDto.getUsername(), true, validationUtil.hashPassword(registerUserDto.getPassword()));
        return true;
    }

    public Optional<LoginResponse> createUser(CreateUserDto createUserDto) {
        if (!validationUtil.validateUserNew(createUserDto.getUsername())) return Optional.empty();
        if (roleRepository.findRoleEntityByName(createUserDto.getRole()).isEmpty()) return Optional.empty();
        UserEntity user = new UserEntity(createUserDto.getUsername(), validationUtil.hashPassword(createUserDto.getPassword()), false, roleRepository.findRoleEntityByName(createUserDto.getRole()).get());
        userRepository.save(user);
        return Optional.of(new LoginResponse(host + "auth/signup.html?username=" + user.getUsername() + "&token=" + generateToken(user, signupLinkExpires), signupLinkExpires, createUserDto.getRole()));
    }


    public Optional<LoginResponse> login(LoginUserDto loginUserDto) {
        if (!validationUtil.loginCheck(loginUserDto.getUsername())) return Optional.empty();
        UserEntity user = userRepository.findByUsername(loginUserDto.getUsername()).get();
        if (!user.getPassword().equals(validationUtil.hashPassword(loginUserDto.getPassword())))
            return Optional.empty();
        return Optional.of(new LoginResponse(generateToken(user, expirationTime), expirationTime, user.getRole().toString()));
    }


    private String generateToken(UserEntity user, Long expirationTime) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().toString());
        return jwtUtil.generateToken(user.getUsername(), claims, expirationTime);
    }
}
