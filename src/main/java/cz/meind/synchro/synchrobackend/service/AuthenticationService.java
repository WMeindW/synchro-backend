package cz.meind.synchro.synchrobackend.service;

import cz.meind.synchro.synchrobackend.database.entities.RoleEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateUserDto;
import cz.meind.synchro.synchrobackend.dto.request.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.request.RegisterUserDto;
import cz.meind.synchro.synchrobackend.dto.response.LoginResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final JwtUtil jwtUtil;

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

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    private void initializeAdmin() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            if (roleRepository.findRoleEntityByName("ADMIN").isEmpty()) {
                roleRepository.save(new RoleEntity("ADMIN"));
            }
            userRepository.save(new UserEntity(adminUsername, hashPassword(adminPassword), true, roleRepository.findRoleEntityByName("ADMIN").get()));
        }
        if (roleRepository.findRoleEntityByName(defaultRole).isEmpty())
            roleRepository.save(new RoleEntity(defaultRole));
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean signup(RegisterUserDto registerUserDto) {
        if (!usernameExists(registerUserDto.getUsername())) return false;
        if (validateUsername(registerUserDto.getUsername())) return false;
        userRepository.updateUserEnabledAndPasswordByUsername(registerUserDto.getUsername(), true, hashPassword(registerUserDto.getPassword()));
        return true;
    }

    public Optional<LoginResponse> createUser(CreateUserDto createUserDto) {
        if (usernameExists(createUserDto.getUsername())) return Optional.empty();
        if (validateUsername(createUserDto.getUsername())) return Optional.empty();

        if (roleRepository.findRoleEntityByName(createUserDto.getRole()).isEmpty()) return Optional.empty();
        UserEntity user = new UserEntity(createUserDto.getUsername(), hashPassword(createUserDto.getPassword()), false, roleRepository.findRoleEntityByName(createUserDto.getRole()).get());
        userRepository.save(user);
        LoginResponse response = new LoginResponse();
        response.setToken(host + "auth/signup?username=" + user.getUsername() + "&token=" + generateToken(user, signupLinkExpires));
        response.setExpiresIn(signupLinkExpires);
        response.setRole(createUserDto.getRole());
        return Optional.of(response);
    }

    private boolean validateUsername(String username) {
        return !username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    public Optional<LoginResponse> login(LoginUserDto loginUserDto) {
        if (!usernameExists(loginUserDto.getUsername())) return Optional.empty();
        if (validateUsername(loginUserDto.getUsername())) return Optional.empty();
        UserEntity user = userRepository.findByUsername(loginUserDto.getUsername()).get();
        if (user.getPassword().equals(hashPassword(loginUserDto.getPassword()))) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(generateToken(user, expirationTime));
            loginResponse.setExpiresIn(expirationTime);
            loginResponse.setRole(user.getRole().toString());
            System.out.println(jwtUtil.extractClaims(loginResponse.getToken()));
            return Optional.of(loginResponse);
        }
        return Optional.empty();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            // Convert the byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private String generateToken(UserEntity user, Long expirationTime) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().toString());
        return jwtUtil.generateToken(user.getUsername(), claims, expirationTime);
    }
}
