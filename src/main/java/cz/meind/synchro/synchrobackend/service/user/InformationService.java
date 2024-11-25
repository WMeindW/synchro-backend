package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.database.entities.MotdEntity;
import cz.meind.synchro.synchrobackend.database.repositories.MotdRepository;
import cz.meind.synchro.synchrobackend.dto.request.MotdDto;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class InformationService {
    private final MotdRepository motdRepository;
    private final ValidationUtil validationUtil;

    public InformationService(MotdRepository motdRepository, ValidationUtil validationUtil) {
        this.motdRepository = motdRepository;
        this.validationUtil = validationUtil;
    }

    @PostConstruct
    public void init() {

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
