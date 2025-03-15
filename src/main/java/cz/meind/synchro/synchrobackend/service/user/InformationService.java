package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.entities.*;
import cz.meind.synchro.synchrobackend.database.repositories.*;
import cz.meind.synchro.synchrobackend.dto.response.InfoResponse;
import cz.meind.synchro.synchrobackend.dto.response.SummaryResponse;
import cz.meind.synchro.synchrobackend.dto.response.UserValueResponseEntity;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InformationService {
    private final MotdRepository motdRepository;
    private final ValidationUtil validationUtil;
    private final UserRepository userRepository;
    private final SynchroConfig synchroConfig;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;
    private final CheckRepository checkRepository;

    /**
     * Constructor for InformationService.
     *
     * @param motdRepository  Repository to handle Messages of the Day (MOTD).
     * @param validationUtil  Utility class for validation.
     * @param userRepository  Repository to handle user data.
     * @param synchroConfig   Configuration for synchronization.
     * @param roleRepository  Repository to handle role data.
     * @param eventRepository Repository to handle event data.
     * @param checkRepository Repository to handle check-in/check-out data.
     */
    public InformationService(MotdRepository motdRepository, ValidationUtil validationUtil, UserRepository userRepository, SynchroConfig synchroConfig, RoleRepository roleRepository, EventRepository eventRepository, EventTypeRepository eventTypeRepository, CheckRepository checkRepository) {
        this.motdRepository = motdRepository;
        this.validationUtil = validationUtil;
        this.userRepository = userRepository;
        this.synchroConfig = synchroConfig;
        this.roleRepository = roleRepository;
        this.eventRepository = eventRepository;
        this.checkRepository = checkRepository;
    }

    /**
     * Queries the summary of users' event and check-in data for a given month.
     *
     * @param month The month for which the summary is being queried.
     * @return A SummaryResponse object containing the calculated and checked-in times for each user.
     */
    public SummaryResponse querySummary(LocalDate month) {
        List<UserValueResponseEntity> responseObjects = new ArrayList<>();
        List<EventEntity> events = eventRepository.findAllByMonthAndYear(month.getMonthValue(), month.getYear());
        List<CheckEntity> checkEntities = checkRepository.findAllByMonthAndYear(month.getMonthValue(), month.getYear());
        List<UserEntity> users = userRepository.findUserEntitiesByEnabled(true);
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.DOWN);

        // Loop over all users to calculate their event and check-in durations
        for (UserEntity user : users) {
            float calculated = events.stream().filter(e -> e.getUser().equals(user)).map(e -> Duration.between(e.getTimeStart().toLocalDateTime(), e.getTimeEnd().toLocalDateTime()).toMinutes()).mapToLong(Long::longValue).sum();
            float checked = checkEntities.stream().filter(c -> c.getUser().equals(user) && c.getCheckOut() != null).map(c -> Duration.between(c.getCheckIn().toLocalDateTime(), c.getCheckOut().toLocalDateTime()).toMinutes()).mapToLong(Long::longValue).sum();
            responseObjects.add(new UserValueResponseEntity(user.getUsername(), df.format(calculated / 60), df.format(checked / 60)));
        }
        return new SummaryResponse(responseObjects);
    }

    /**
     * Queries system information like active users, event types, and roles.
     *
     * @return InfoResponse containing users, event types, and roles.
     */
    public InfoResponse queryInfo() {
        List<String> users = userRepository.findAll().stream().filter(UserEntity::getEnabled).map(UserEntity::getUsername).toList();
        List<String> roles = roleRepository.findAll().stream().map(RoleEntity::getName).toList();
        return new InfoResponse(users, synchroConfig.getEventTypeList(), roles);
    }

    /**
     * Retrieves the current Message of the Day (MOTD).
     *
     * @return The content of the latest MOTD, or an empty string if no MOTD exists.
     */
    public String queryMotd() {
        if (motdRepository.findMaxIdEntity().isPresent()) return motdRepository.findMaxIdEntity().get().getContent();
        return "";
    }

    /**
     * Validates a given Message of the Day (MOTD).
     *
     * @param motd The Message of the Day to validate.
     * @return A valid MOTD string or an empty string if invalid.
     */
    public String testMotd(String motd) {
        return validationUtil.validateMotd(motd);
    }

    /**
     * Saves a new Message of the Day (MOTD) after validating it.
     *
     * @param motd The Message of the Day to save.
     */
    @Async
    public void saveMotd(String motd) {
        motd = validationUtil.validateMotd(motd);
        if (motd.isEmpty()) return;
        motdRepository.save(new MotdEntity(validationUtil.validateMotd(motd)));
    }
}