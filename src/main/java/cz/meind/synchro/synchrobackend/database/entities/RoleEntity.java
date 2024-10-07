package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "role")
    private Collection<UserEntity> users;

    public RoleEntity(String name) {
        this.name = name;
    }

    public RoleEntity() {

    }
}

