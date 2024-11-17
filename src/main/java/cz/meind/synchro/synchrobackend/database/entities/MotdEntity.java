package cz.meind.synchro.synchrobackend.database.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "motd")
public class MotdEntity {
    @Id
    private Long id;

    private String content;
}
