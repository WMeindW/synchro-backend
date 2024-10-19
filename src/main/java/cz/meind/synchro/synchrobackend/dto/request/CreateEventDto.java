package cz.meind.synchro.synchrobackend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
public class CreateEventDto {
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String username;

    @Override
    public String toString() {
        return "CreateEventDto{" + "type='" + type + '\'' + ", start=" + start + ", end=" + end + ", username='" + username + '\'' + '}';
    }
}
