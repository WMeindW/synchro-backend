package cz.meind.synchro.synchrobackend.database.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "motd")
public class MotdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private Timestamp date;

    public MotdEntity(String content) {
        this.content = content;
        this.date = Timestamp.valueOf(LocalDateTime.now());
    }

    public MotdEntity() {

    }
}
