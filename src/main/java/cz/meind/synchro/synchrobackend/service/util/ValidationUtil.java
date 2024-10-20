package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.List;

@Component
public class ValidationUtil {
    private final EventRepository eventRepository;
    UserRepository userRepository;

    public ValidationUtil(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
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

    public boolean validateEvent(CreateEventDto createEventDto) {
        if (Timestamp.valueOf(createEventDto.getEnd()).before(Timestamp.valueOf(createEventDto.getStart()))) return false;
        for (EventEntity event : eventRepository.findAllByUser(userRepository.findByUsername(createEventDto.getUsername()).get()))
            if (event.getTimeEnd().before(Timestamp.valueOf(createEventDto.getStart())) || Timestamp.valueOf(createEventDto.getEnd()).before(event.getTimeStart()))
                return false;
        return true;
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
