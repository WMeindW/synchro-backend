package cz.meind.synchro.synchrobackend.service;

import cz.meind.synchro.synchrobackend.database.entities.RoleEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.LoginResponse;
import cz.meind.synchro.synchrobackend.dto.LoginUserDto;
import cz.meind.synchro.synchrobackend.dto.RegisterUserDto;
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

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean signup(RegisterUserDto registerUserDto) {
        if (usernameExists(registerUserDto.getUsername())) return false;
        //if (!validateUsername(registerUserDto.getUsername())) return false;
        if (roleRepository.findRoleEntityByName(defaultRole).isEmpty())
            roleRepository.save(new RoleEntity(defaultRole));
        UserEntity user = new UserEntity();
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(hashPassword(registerUserDto.getPassword()));
        user.setRole(roleRepository.findRoleEntityByName(defaultRole).get());
        userRepository.save(user);
        return true;
    }

    private boolean validateUsername(String username) {
        return username.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public Optional<LoginResponse> login(LoginUserDto loginUserDto) {
        if (!usernameExists(loginUserDto.getUsername())) return Optional.empty();
        //if (!validateUsername(loginUserDto.getUsername())) return Optional.empty();
        UserEntity user = userRepository.findByUsername(loginUserDto.getUsername()).get();
        if (user.getPassword().equals(hashPassword(loginUserDto.getPassword()))) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(generateToken(user));
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

    private String generateToken(UserEntity user) {
        Map<String, String> claims = new HashMap<>();
        claims.put("role", user.getRole().toString());
        return jwtUtil.generateToken(user.getUsername(), claims);
    }
}
