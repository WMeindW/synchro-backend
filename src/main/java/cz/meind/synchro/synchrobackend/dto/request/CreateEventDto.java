package cz.meind.synchro.synchrobackend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class CreateEventDto {
    private String type;
    private Timestamp start;
    private Timestamp end;
    private String username;
}
