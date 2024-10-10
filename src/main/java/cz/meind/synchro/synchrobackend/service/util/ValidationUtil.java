package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class ValidationUtil {
    UserRepository userRepository;

    public boolean checkUserNotValid(String username) {
        if (!usernameExists(username)) return true;
        if (!validateUsername(username)) return true;
        return userRepository.findByUsername(username).get().getEnabled();
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
}
