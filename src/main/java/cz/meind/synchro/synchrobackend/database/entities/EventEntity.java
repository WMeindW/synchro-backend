package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.sql.Timestamp;

@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Timestamp timeStart;

    @Column(nullable = false)
    private Timestamp timeEnd;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private EventTypeEntity type;

    public EventEntity(EventTypeEntity type, UserEntity user, Timestamp timeEnd, Timestamp timeStart) {
        this.type = type;
        this.user = user;
        this.timeEnd = timeEnd;
        this.timeStart = timeStart;
    }

    public EventEntity() {

    }

    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", user=" + user +
                ", type=" + type +
                '}';
    }
}
