package cz.meind.synchro.synchrobackend.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private boolean checkedIn;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToOne()
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<EventEntity> events;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<EventEntity> checks;

    public UserEntity(String username, String password, Boolean enabled, RoleEntity role) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
        this.checkedIn = false;
    }

    public UserEntity(RoleEntity role, String email, String phone, Boolean enabled, String username, String password) {
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.checkedIn = false;
        this.enabled = enabled;
        this.password = password;
        this.username = username;
    }

    public UserEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(username, that.username);
    }



    @Override
    public String toString() {
        return "UserEntity{" + "id=" + id + ", username='" + username + '\'' + ", password='" + password + '\'' + ", enabled=" + enabled + ", role=" + role + '}';
    }
}
