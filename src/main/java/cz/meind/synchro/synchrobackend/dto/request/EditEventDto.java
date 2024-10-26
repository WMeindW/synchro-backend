package cz.meind.synchro.synchrobackend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class EditEventDto {
    private Long id;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String username;

    @Override
    public String toString() {
        return "EditEventDto{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", username='" + username + '\'' +
                '}';
    }
}
