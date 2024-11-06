package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.CheckEntity;
import cz.meind.synchro.synchrobackend.database.repositories.CheckRepository;
import cz.meind.synchro.synchrobackend.database.repositories.UserRepository;
import cz.meind.synchro.synchrobackend.service.user.auth.SecurityService;
import cz.meind.synchro.synchrobackend.service.util.JwtUtil;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class AttendanceService {
    private final JwtUtil jwtUtil;
    private final SecurityService securityService;
    private final SynchroConfig synchroConfig;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final CheckRepository checkRepository;

    public AttendanceService(JwtUtil jwtUtil, SecurityService securityService, SynchroConfig synchroConfig, UserRepository userRepository, ValidationUtil validationUtil, CheckRepository checkRepository) {
        this.jwtUtil = jwtUtil;
        this.securityService = securityService;
        this.synchroConfig = synchroConfig;
        this.userRepository = userRepository;
        this.validationUtil = validationUtil;
        this.checkRepository = checkRepository;
    }

    public Boolean isCheckedIn(HttpServletRequest request, String username) {
        if (!validationUtil.loginCheck(username)) return false;
        // if (!hasPermissions(request, username)) return false;
        return userRepository.findByUsername(username).get().isCheckedIn();
    }

    public boolean checkUser(HttpServletRequest request, String username) {
        if (!validationUtil.loginCheck(username)) return false;
        //if (!hasPermissions(request, username)) return false;
        updatedCheckedUser(username);
        updatedCheckEntity(username);
        return true;
    }

    @Async
    protected void updatedCheckEntity(String username) {
        if (userRepository.findByUsername(username).get().isCheckedIn()) {
            checkRepository.updateChecked(userRepository.findByUsername(username).get(), Timestamp.valueOf(LocalDateTime.now()));
        } else {
            checkRepository.save(new CheckEntity(userRepository.findByUsername(username).get(), Timestamp.valueOf(LocalDateTime.now())));
        }
    }

    @Async
    protected void updatedCheckedUser(String username) {
        userRepository.updateUserChecked(username, !userRepository.findByUsername(username).get().isCheckedIn());
    }

    private boolean hasPermissions(HttpServletRequest request, String username) {
        return jwtUtil.extractClaims(securityService.extractCookie(request)).getSubject().equals(username) || jwtUtil.extractClaims(securityService.extractCookie(request)).get("role").toString().equals(synchroConfig.getAdminRole());
    }
}
