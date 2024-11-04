package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
@Entity
@Table(name = "check")
public class CheckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp checkIn;

    private Timestamp checkOut;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public CheckEntity(Timestamp checkIn, UserEntity user) {
        this.checkIn = checkIn;
        this.user = user;
    }
}
