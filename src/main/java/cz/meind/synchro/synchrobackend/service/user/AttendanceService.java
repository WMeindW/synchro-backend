package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.CheckEntity;
import cz.meind.synchro.synchrobackend.database.repositories.CheckRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.dto.response.EventResponseEntity;
import cz.meind.synchro.synchrobackend.dto.response.EventsResponse;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Service class responsible for handling attendance-related functionality such as checking in users, querying attendance,
 * and updating attendance records.
 */
@Service
public class AttendanceService {

    private final JwtUtil jwtUtil;
    private final SecurityService securityService;
    private final SynchroConfig synchroConfig;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final CheckRepository checkRepository;

    /**
     * Constructs an instance of AttendanceService with the specified dependencies.
     *
     * @param jwtUtil         Utility for handling JWT tokens.
     * @param securityService Service for handling security-related tasks.
     * @param synchroConfig   Configuration class containing synchronization settings.
     * @param userRepository  Repository for accessing user data.
     * @param validationUtil  Utility for validation checks.
     * @param checkRepository Repository for accessing attendance check data.
     */
    public AttendanceService(JwtUtil jwtUtil, SecurityService securityService, SynchroConfig synchroConfig, UserRepository userRepository, ValidationUtil validationUtil, CheckRepository checkRepository) {
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
        this.synchroConfig = synchroConfig;
        this.userRepository = userRepository;
        this.validationUtil = validationUtil;
        this.checkRepository = checkRepository;
    }

    /**
     * Checks if a user is currently checked in.
     *
     * @param request  The HTTP request containing the JWT token.
     * @param username The username of the user to check.
     * @return true if the user is checked in, false otherwise.
     */
    public Boolean isCheckedIn(HttpServletRequest request, String username) {
        if (!validationUtil.loginCheck(username)) return false;
        if (!hasPermissions(request, username)) return false;
        return userRepository.findByUsername(username).get().isCheckedIn();
    }

    /**
     * Checks in or checks out a user based on their current attendance status.
     * Updates the attendance record accordingly.
     *
     * @param request  The HTTP request containing the JWT token.
     * @param username The username of the user to check.
     * @return true if the user's attendance was successfully updated, false otherwise.
     */
    public boolean checkUser(HttpServletRequest request, String username) {
        if (!validationUtil.loginCheck(username)) return false;
        if (!hasPermissions(request, username)) return false;
        updatedCheckedUser(username);
        updatedCheckEntity(username);
        return true;
    }

    /**
     * Queries the attendance records for all users who have checked out.
     *
     * @return A response containing the list of attendance records for users who have checked out.
     */
    public EventsResponse queryAttendance() {
        return new EventsResponse(checkRepository.findAll().stream().filter(checkEntity -> checkEntity.getCheckOut() != null).map(eventEntity -> new EventResponseEntity(eventEntity.getId(), eventEntity.getCheckIn().toLocalDateTime(), eventEntity.getCheckOut().toLocalDateTime(), eventEntity.getUser().getUsername(), synchroConfig.getWorkPeriod())).toList());
    }

    /**
     * Updates the attendance check entity for the user by either creating a new record or updating the existing one.
     *
     * @param username The username of the user whose attendance record needs to be updated.
     */
    @Async
    protected void updatedCheckEntity(String username) {
        if (userRepository.findByUsername(username).get().isCheckedIn()) {
            checkRepository.updateChecked(userRepository.findByUsername(username).get(), Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
        } else {
            checkRepository.save(new CheckEntity(userRepository.findByUsername(username).get(), Timestamp.valueOf(LocalDateTime.now().plusHours(1))));
        }
    }

    /**
     * Updates the checked-in status of the user.
     *
     * @param username The username of the user to update the check-in status.
     */
    @Async
    protected void updatedCheckedUser(String username) {
        userRepository.updateUserChecked(username, !userRepository.findByUsername(username).get().isCheckedIn());
    }

    /**
     * Checks if the current request has permissions to access a user's attendance data based on their role.
     *
     * @param request  The HTTP request containing the JWT token.
     * @param username The username of the user whose attendance data is being accessed.
     * @return true if the request has valid permissions to access the user's attendance, false otherwise.
     */
    private boolean hasPermissions(HttpServletRequest request, String username) {
        Claims c = jwtUtil.extractClaims(securityService.extractCookie(request));
        if (c == null) return false;
        return c.get("role").toString().equals(synchroConfig.getAdminRole()) || c.getSubject().equals(username);
    }
}
