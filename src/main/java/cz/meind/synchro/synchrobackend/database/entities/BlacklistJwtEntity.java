package cz.meind.synchro.synchrobackend.database.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
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
