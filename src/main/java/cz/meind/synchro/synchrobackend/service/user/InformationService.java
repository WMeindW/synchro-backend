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

    public InformationService(MotdRepository motdRepository, ValidationUtil validationUtil, UserRepository userRepository, SynchroConfig synchroConfig, RoleRepository roleRepository, EventRepository eventRepository, EventTypeRepository eventTypeRepository, CheckRepository checkRepository) {
        this.motdRepository = motdRepository;
        this.validationUtil = validationUtil;
        this.userRepository = userRepository;
        this.synchroConfig = synchroConfig;
        this.roleRepository = roleRepository;
        this.eventRepository = eventRepository;
        this.checkRepository = checkRepository;
    }

    public SummaryResponse querySummary(LocalDate month) {
        List<UserValueResponseEntity> responseObjects = new ArrayList<>();
        List<EventEntity> events = eventRepository.findAllByMonthAndYear(month.getMonthValue(), month.getYear());
        List<CheckEntity> checkEntities = checkRepository.findAllByMonthAndYear(month.getMonthValue(), month.getYear());
        List<UserEntity> users = userRepository.findUserEntitiesByEnabled(true);
        for (UserEntity user : users) {
            float calculated = events.stream().filter(e -> e.getUser().equals(user)).map(e -> Duration.between(e.getTimeStart().toLocalDateTime(), e.getTimeEnd().toLocalDateTime()).toMinutes()).mapToLong(Long::longValue).sum();
            float checked = checkEntities.stream().filter(c -> c.getUser().equals(user)).map(c -> Duration.between(c.getCheckIn().toLocalDateTime(), c.getCheckOut().toLocalDateTime()).toMinutes()).mapToLong(Long::longValue).sum();
            responseObjects.add(new UserValueResponseEntity(user.getUsername(), Float.toString(calculated / 60), Float.toString(checked / 60)));
        }
        return new SummaryResponse(responseObjects);
    }

    public InfoResponse queryInfo() {
        List<String> users = userRepository.findAll().stream().filter(UserEntity::getEnabled).map(UserEntity::getUsername).toList();
        List<String> roles = roleRepository.findAll().stream().map(RoleEntity::getName).toList();
        return new InfoResponse(users, synchroConfig.getEventTypeList(), roles);
    }

    public String queryMotd() {
        if (motdRepository.findMaxIdEntity().isPresent()) return motdRepository.findMaxIdEntity().get().getContent();
        return "";
    }

    public String testMotd(String motd) {
        if (motdRepository.findMaxIdEntity().isPresent()) return validationUtil.validateMotd(motd);
        return "";
    }

    @Async
    public void saveMotd(String motd) {
        motd = validationUtil.validateMotd(motd);
        if (motd.isEmpty()) return;
        motdRepository.save(new MotdEntity(validationUtil.validateMotd(motd)));
    }
}
