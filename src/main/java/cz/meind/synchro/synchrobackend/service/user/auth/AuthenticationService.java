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

/**
 * Service class responsible for handling authentication-related tasks such as user signup, login, and logout.
 * It also initializes necessary roles and admin users on startup.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    private final SynchroConfig config;
    private final BlacklistJwtRepository blacklistJwtRepository;
    private final SecurityService securityService;

    /**
     * Constructs an instance of AuthenticationService with the specified dependencies.
     *
     * @param userRepository         Repository for accessing user data.
     * @param roleRepository         Repository for accessing role data.
     * @param jwtUtil                Utility for handling JWTs.
     * @param validationUtil         Utility for validation tasks.
     * @param config                 Configuration for the application.
     * @param blacklistJwtRepository Repository for accessing the blacklist of JWTs.
     * @param securityService        Service for managing security-related operations.
     */
    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil, ValidationUtil validationUtil, SynchroConfig config, BlacklistJwtRepository blacklistJwtRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.validationUtil = validationUtil;
        this.config = config;
        this.blacklistJwtRepository = blacklistJwtRepository;
        this.securityService = securityService;
    }

    /**
     * Initializes the admin user and role if they don't already exist in the database.
     * This is executed at application startup.
     */
    @PostConstruct
    private void initializeAdmin() {
        if (userRepository.findByUsername(config.getAdminUsername()).isPresent()) return;
        if (roleRepository.findRoleEntityByName(config.getAdminRole()).isPresent()) return;
        roleRepository.save(new RoleEntity(config.getAdminRole()));
        userRepository.save(new UserEntity(config.getAdminUsername(), validationUtil.hashPassword(config.getAdminPassword()), true, roleRepository.findRoleEntityByName("ADMIN").get()));
    }

    /**
     * Initializes the default user role if it doesn't already exist in the database.
     * This is executed at application startup.
     */
    @PostConstruct
    private void initializeUser() {
        if (roleRepository.findRoleEntityByName(config.getDefaultRole()).isEmpty())
            roleRepository.save(new RoleEntity(config.getDefaultRole()));
    }

    /**
     * Logs out a user by adding the JWT to the blacklist.
     *
     * @param request The HTTP request containing the user's JWT.
     */
    public void logout(HttpServletRequest request) {
        blacklistJwtRepository.save(new BlacklistJwtEntity(securityService.extractCookie(request)));
    }

    /**
     * Signs up a new user by updating their password and enabling the account.
     * Verifies that the token matches the username in the request.
     *
     * @param registerUserDto Data transfer object containing the registration details.
     * @return true if the signup is successful, false otherwise.
     */
    public boolean signup(RegisterUserDto registerUserDto) {
        if (!validationUtil.signupCheck(registerUserDto.getUsername())) return false;
        if (!registerUserDto.getUsername().equals(jwtUtil.extractClaims(registerUserDto.getToken()).getSubject()))
            return false;
        userRepository.updateUserEnabledAndPasswordByUsername(registerUserDto.getUsername(), true, validationUtil.hashPassword(registerUserDto.getPassword()));
        return true;
    }

    /**
     * Creates a new user account.
     * It validates the user data, checks the email and phone, and ensures the role exists before creating the user.
     * A signup token is generated and sent to the user for account activation.
     *
     * @param createUserDto Data transfer object containing user creation details.
     * @return An optional containing the login response with a token if the user is successfully created, or an empty optional otherwise.
     */
    public Optional<LoginResponse> createUser(CreateUserDto createUserDto) {
        if (!validationUtil.validateUserNew(createUserDto.getUsername())) return Optional.empty();
        if (!validationUtil.validateEmail(createUserDto.getEmail()) || !validationUtil.validatePhone(createUserDto.getPhone()))
            return Optional.empty();
        if (roleRepository.findRoleEntityByName(createUserDto.getRole()).isEmpty()) return Optional.empty();
        UserEntity user = new UserEntity(roleRepository.findRoleEntityByName(createUserDto.getRole()).get(), createUserDto.getEmail(), createUserDto.getPhone(), false, createUserDto.getUsername(), validationUtil.hashPassword(createUserDto.getPassword()));
        userRepository.save(user);
        return Optional.of(new LoginResponse(config.getHost() + "auth/signup.html?username=" + user.getUsername() + "&token=" + generateToken(user, config.getSignupLinkExpires()), config.getSignupLinkExpires(), createUserDto.getRole(), createUserDto.getUsername()));
    }

    /**
     * Logs in a user by validating their credentials and generating a JWT token.
     *
     * @param loginUserDto Data transfer object containing login details.
     * @return An optional containing the login response with a token if the login is successful, or an empty optional otherwise.
     */
    public Optional<LoginResponse> login(LoginUserDto loginUserDto) {
        if (!validationUtil.loginCheck(loginUserDto.getUsername())) return Optional.empty();
        UserEntity user = userRepository.findByUsername(loginUserDto.getUsername()).get();
        if (!user.getPassword().equals(validationUtil.hashPassword(loginUserDto.getPassword())))
            return Optional.empty();
        return Optional.of(new LoginResponse(generateToken(user, config.getExpirationTime()), config.getExpirationTime(), user.getRole().toString(), user.getUsername()));
    }

    /**
     * Generates a JWT token for the given user with the specified expiration time.
     *
     * @param user           The user for whom the token is being generated.
     * @param expirationTime The expiration time for the token.
     * @return The generated JWT token as a string.
     */
    private String generateToken(UserEntity user, Long expirationTime) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().toString());
        return jwtUtil.generateToken(user.getUsername(), claims, expirationTime);
    }
}

