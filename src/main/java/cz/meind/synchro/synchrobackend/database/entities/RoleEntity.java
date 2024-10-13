package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Collection;

@Cacheable
@Entity
@Getter
@Setter
@Table(name = "roles")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Collection<UserEntity> users;

    public RoleEntity(String name) {
        this.name = name;
    }

    public RoleEntity() {

    }

    @Override
    public String toString() {
        return name;
    }
}

