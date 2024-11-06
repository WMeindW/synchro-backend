package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.sql.Timestamp;

@Cacheable
@Entity
@Getter
@Setter
@Table(name = "checks")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CheckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp checkIn;

    private Timestamp checkOut;

    private Boolean checked;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public CheckEntity(UserEntity user, Timestamp checkIn) {
        this.user = user;
        this.checkIn = checkIn;
        this.checked = true;
    }

    public CheckEntity() {

    }
}

