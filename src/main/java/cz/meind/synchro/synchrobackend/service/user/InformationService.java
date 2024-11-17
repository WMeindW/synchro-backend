package cz.meind.synchro.synchrobackend.service.user;

import cz.meind.synchro.synchrobackend.database.entities.MotdEntity;
import cz.meind.synchro.synchrobackend.database.repositories.MotdRepository;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
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

    public String queryMotd() {
        if (motdRepository.findMaxIdEntity() != null) return motdRepository.findMaxIdEntity().getContent();
        return "";
    }

    @Async
    public void saveMotd(String motd) {
        motdRepository.save(new MotdEntity(validationUtil.validateMotd(motd)));
    }
}
