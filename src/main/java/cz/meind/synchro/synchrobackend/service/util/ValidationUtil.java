package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import cz.meind.synchro.synchrobackend.database.repositories.EventRepository;
import cz.meind.synchro.synchrobackend.database.repositories.RoleRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditEventDto;
import cz.meind.synchro.synchrobackend.dto.request.EditUserDto;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Objects;

@Service
public class ValidationUtil {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Constructor for ValidationUtil.
     *
     * @param userRepository  Repository for interacting with User entities.
     * @param eventRepository Repository for interacting with Event entities.
     * @param roleRepository  Repository for interacting with Role entities.
     */
    public ValidationUtil(UserRepository userRepository, EventRepository eventRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Checks if the username exists, is valid, and the user is enabled for login.
     *
     * @param username The username to check.
     * @return True if the username exists, is valid, and the user is enabled, otherwise false.
     */
    public boolean loginCheck(String username) {
        if (!usernameExists(username)) return false;
        if (!validateUsername(username)) return false;
        return userRepository.findByUsername(username).get().getEnabled();
    }

    /**
     * Checks if the username exists, is valid, and the user is not enabled for signup.
     *
     * @param username The username to check.
     * @return True if the username exists, is valid, and the user is not enabled, otherwise false.
     */
    public boolean signupCheck(String username) {
        if (!usernameExists(username)) return false;
        if (!validateUsername(username)) return false;
        return !userRepository.findByUsername(username).get().getEnabled();
    }

    /**
     * Checks the validity of a user's edited information (role, email, phone, and username).
     *
     * @param editUserDto The data to validate.
     * @return True if the edited user information is valid, otherwise false.
     */
    public boolean editCheck(EditUserDto editUserDto) {
        if (roleRepository.findRoleEntityByName(editUserDto.getRole()).isEmpty()) return false;
        if (editUserDto.getEmail().length() > 60 || editUserDto.getPhone().length() > 12) return false;
        if (!validateUsername(editUserDto.getUsername())) return false;
        return userRepository.findByUsername(editUserDto.getUsername()).isEmpty() || userRepository.findByUsername(editUserDto.getUsername()).get().getId().equals(editUserDto.getId());
    }

    /**
     * Validates if a username is available for new user registration.
     *
     * @param username The username to validate.
     * @return True if the username is available and valid, otherwise false.
     */
    public boolean validateUserNew(String username) {
        if (usernameExists(username)) return false;
        return validateUsername(username);
    }

    /**
     * Validates if an event's start and end times do not overlap with existing events.
     *
     * @param createEventDto The event data to validate.
     * @return True if the event's times are valid, otherwise false.
     */
    public boolean validateEvent(CreateEventDto createEventDto) {
        for (EventEntity event : eventRepository.findAllByUser(userRepository.findByUsername(createEventDto.getUsername()).get()))
            if (!event.isDeleted() && !(event.getTimeEnd().before(Timestamp.valueOf(createEventDto.getStart())) || Timestamp.valueOf(createEventDto.getEnd()).before(event.getTimeStart())))
                return false;
        return !(Timestamp.valueOf(createEventDto.getEnd()).before(Timestamp.valueOf(createEventDto.getStart())));
    }

    /**
     * Validates if an event's edited start and end times do not overlap with other events.
     *
     * @param editEventDto The edited event data to validate.
     * @return True if the edited event's times are valid, otherwise false.
     */
    public boolean validateEventEdit(EditEventDto editEventDto) {
        for (EventEntity event : eventRepository.findAllByUser(userRepository.findByUsername(editEventDto.getUsername()).get()))
            if (!event.isDeleted() && !Objects.equals(event.getId(), editEventDto.getId()) && !(event.getTimeEnd().before(Timestamp.valueOf(editEventDto.getStart())) || Timestamp.valueOf(editEventDto.getEnd()).before(event.getTimeStart())))
                return false;
        return !(Timestamp.valueOf(editEventDto.getEnd()).before(Timestamp.valueOf(editEventDto.getStart())));
    }

    /**
     * Validates the format of an email address.
     *
     * @param email The email to validate.
     * @return True if the email matches a valid pattern, otherwise false.
     */
    public boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Cleans and validates a Message of the Day (MOTD) string, allowing safe HTML tags and attributes.
     *
     * @param motd The message to validate.
     * @return The cleaned message, allowing only certain HTML tags and attributes.
     */
    public String validateMotd(String motd) {
        Safelist safelist = Safelist.none().addTags("div", "p", "h1", "h2", "h3", "h4", "h5", "a").addAttributes("a", "href", "style").addAttributes("div", "style").addAttributes("p", "style").addAttributes("h1", "style").addAttributes("h2", "style").addAttributes("h3", "style").addAttributes("h4", "style").addAttributes("h5", "style");
        return Jsoup.clean(motd, safelist);
    }

    /**
     * Validates the format of a phone number.
     *
     * @param phone The phone number to validate.
     * @return True if the phone number matches the international format, otherwise false.
     */
    public boolean validatePhone(String phone) {
        return phone.matches("^\\+?[1-9]\\d{1,14}$");
    }

    /**
     * Checks if a username exists in the system.
     *
     * @param username The username to check.
     * @return True if the username exists, otherwise false.
     */
    private boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Validates the format of a username, ensuring it follows the given pattern.
     *
     * @param username The username to validate.
     * @return True if the username is valid, otherwise false.
     */
    private boolean validateUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password The password to hash.
     * @return The hashed password as a hex string.
     */
    public String hashPassword(String password) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : toHashBytes(password)) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Converts a password into a byte array, which is then hashed using SHA-256.
     *
     * @param password The password to convert and hash.
     * @return The hashed byte array.
     */
    @SneakyThrows
    private byte[] toHashBytes(String password) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes());
    }
}