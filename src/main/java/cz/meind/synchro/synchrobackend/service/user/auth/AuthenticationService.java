package cz.meind.synchro.synchrobackend.service.user.auth;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.BlacklistJwtEntity;
import cz.meind.synchro.synchrobackend.database.entities.RoleEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.BlacklistJwtRepository;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateUserDto;
import cz.meind.synchro.synchrobackend.dto.request.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.request.RegisterUserDto;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtUtil jwtUtil;

    private final ValidationUtil validationUtil;

    private final SynchroConfig config;
    private final BlacklistJwtRepository blacklistJwtRepository;
    private final SecurityService securityService;


    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil, ValidationUtil validationUtil, SynchroConfig config, BlacklistJwtRepository blacklistJwtRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.validationUtil = validationUtil;
        this.config = config;
        this.blacklistJwtRepository = blacklistJwtRepository;
        this.securityService = securityService;
    }

    @PostConstruct
    private void initializeAdmin() {
        if (userRepository.findByUsername(config.getAdminUsername()).isPresent()) return;
        if (roleRepository.findRoleEntityByName(config.getAdminRole()).isPresent()) return;
        roleRepository.save(new RoleEntity(config.getAdminRole()));
        userRepository.save(new UserEntity(config.getAdminUsername(), validationUtil.hashPassword(config.getAdminPassword()), true, roleRepository.findRoleEntityByName("ADMIN").get()));
    }

    @PostConstruct
    private void initializeUser() {
        if (roleRepository.findRoleEntityByName(config.getDefaultRole()).isEmpty())
            roleRepository.save(new RoleEntity(config.getDefaultRole()));
    }

    public void logout(HttpServletRequest request) {
        blacklistJwtRepository.save(new BlacklistJwtEntity(securityService.extractCookie(request)));
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
        if (!validationUtil.validateEmail(createUserDto.getEmail()) || !validationUtil.validatePhone(createUserDto.getPhone())) return Optional.empty();
        if (roleRepository.findRoleEntityByName(createUserDto.getRole()).isEmpty()) return Optional.empty();
        UserEntity user = new UserEntity(roleRepository.findRoleEntityByName(createUserDto.getRole()).get(), createUserDto.getEmail(), createUserDto.getPhone(), false, createUserDto.getUsername(), validationUtil.hashPassword(createUserDto.getPassword()));
        userRepository.save(user);
        return Optional.of(new LoginResponse(config.getHost() + "auth/signup.html?username=" + user.getUsername() + "&token=" + generateToken(user, config.getSignupLinkExpires()), config.getSignupLinkExpires(), createUserDto.getRole(), createUserDto.getUsername()));
    }


    public Optional<LoginResponse> login(LoginUserDto loginUserDto) {
        if (!validationUtil.loginCheck(loginUserDto.getUsername())) return Optional.empty();
        UserEntity user = userRepository.findByUsername(loginUserDto.getUsername()).get();
        if (!user.getPassword().equals(validationUtil.hashPassword(loginUserDto.getPassword())))
            return Optional.empty();
        return Optional.of(new LoginResponse(generateToken(user, config.getExpirationTime()), config.getExpirationTime(), user.getRole().toString(), user.getUsername()));
    }

    private String generateToken(UserEntity user, Long expirationTime) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().toString());
        return jwtUtil.generateToken(user.getUsername(), claims, expirationTime);
    }
}
