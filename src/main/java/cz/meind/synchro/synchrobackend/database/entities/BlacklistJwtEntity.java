package cz.meind.synchro.synchrobackend.database.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Setter
@Getter
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "blacklist")
public class BlacklistJwtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jwtToken;

    public BlacklistJwtEntity(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public BlacklistJwtEntity() {
    }
}
