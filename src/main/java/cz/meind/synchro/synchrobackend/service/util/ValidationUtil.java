package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditUserDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Objects;

@Service
public class ValidationUtil {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SynchroConfig synchroConfig;
    private final RoleRepository roleRepository;

    public ValidationUtil(UserRepository userRepository, EventRepository eventRepository, SynchroConfig synchroConfig, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.synchroConfig = synchroConfig;
        this.roleRepository = roleRepository;
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

    public boolean editCheck(EditUserDto editUserDto) {
        if (roleRepository.findRoleEntityByName(editUserDto.getRole()).isEmpty()) return false;
        if (editUserDto.getEmail().length() > 60 || editUserDto.getPhone().length() > 12) return false;
        if (!validateUsername(editUserDto.getUsername())) return false;
        return userRepository.findByUsername(editUserDto.getUsername()).isEmpty() || userRepository.findByUsername(editUserDto.getUsername()).get().getId().equals(editUserDto.getId());
    }

    public boolean validateUserNew(String username) {
        if (usernameExists(username)) return false;
        return validateUsername(username);
    }

    public boolean validateEvent(CreateEventDto createEventDto) {
        for (EventEntity event : eventRepository.findAllByUser(userRepository.findByUsername(createEventDto.getUsername()).get()))
            if (!event.isDeleted() && !(event.getTimeEnd().before(Timestamp.valueOf(createEventDto.getStart())) || Timestamp.valueOf(createEventDto.getEnd()).before(event.getTimeStart())))
                return false;
        return !(Timestamp.valueOf(createEventDto.getEnd()).before(Timestamp.valueOf(createEventDto.getStart())));
    }

    public boolean validateEventEdit(EditEventDto editEventDto) {
        for (EventEntity event : eventRepository.findAllByUser(userRepository.findByUsername(editEventDto.getUsername()).get()))
            if (!event.isDeleted() && !Objects.equals(event.getId(), editEventDto.getId()) && !(event.getTimeEnd().before(Timestamp.valueOf(editEventDto.getStart())) || Timestamp.valueOf(editEventDto.getEnd()).before(event.getTimeStart())))
                return false;
        return !(Timestamp.valueOf(editEventDto.getEnd()).before(Timestamp.valueOf(editEventDto.getStart())));
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
