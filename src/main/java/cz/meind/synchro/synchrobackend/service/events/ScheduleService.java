package cz.meind.synchro.synchrobackend.service.events;

import cz.meind.synchro.synchrobackend.dto.request.CreateEventDto;
import cz.meind.synchro.synchrobackend.service.util.ValidationUtil;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final ValidationUtil validationUtil;

    public ScheduleService(ValidationUtil validationUtil) {
        this.validationUtil = validationUtil;
    }

    public boolean createEvent(CreateEventDto createEventDto) {
        if (!checkEvent(createEventDto)) return false;
        System.out.println("Validated");
        return true;
    }

    private boolean checkEvent(CreateEventDto createEventDto) {
        if (!validationUtil.loginCheck(createEventDto.getUsername())) return false;
        return validationUtil.validateEvent(createEventDto);
    }
}
