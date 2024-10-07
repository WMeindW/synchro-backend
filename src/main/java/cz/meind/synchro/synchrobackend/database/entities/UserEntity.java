package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Collection;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserEntity {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne()
    @JoinColumn(name="role_id", nullable=false)
    private RoleEntity role;

    // Constructors
    public UserEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(username, that.username);
    }

}
