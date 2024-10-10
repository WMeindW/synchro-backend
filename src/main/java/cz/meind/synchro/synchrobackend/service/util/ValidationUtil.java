package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class ValidationUtil {
    UserRepository userRepository;

    public ValidationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean loginCheck(String username) {
        if (!usernameExists(username)) return false;
        if (!validateUsername(username)) return false;
        return userRepository.findByUsername(username).get().getEnabled();
    }

    public boolean signupCheck(String username) {
        if (!usernameExists(username)) return false;
        if (!validateUsername(username)) return false;
        return !userRepository.findByUsername(username).get().getEnabled();
    }

    public boolean validateUserNew(String username) {
        if (usernameExists(username)) return false;
        return validateUsername(username);
    }

    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean validateUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    public String hashPassword(String password) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : toHashBytes(password)) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @SneakyThrows
    private byte[] toHashBytes(String password) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes());
    }
}
