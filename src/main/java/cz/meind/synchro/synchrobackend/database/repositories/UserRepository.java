package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.RoleEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.checkedIn = :checkedIn WHERE u.username = :username")
    void updateUserChecked(@Param("username") String username, @Param("checkedIn") boolean checkedIn);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.enabled = :enabled, u.password = :password WHERE u.username = :username")
    void updateUserEnabledAndPasswordByUsername(@Param("username") String username, @Param("enabled") boolean enabled, @Param("password") String password);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.enabled = :enabled WHERE u.username = :username")
    void updateUserEnabledByUsername(@Param("username") String username, @Param("enabled") boolean enabled);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.username = :username, u.password = :password, u.email = :email, u.phone = :phone, u.role = :role WHERE u.id = :id")
    void updateUserEntityById(@Param("id") String id, @Param("username") String username, @Param("password") String password, @Param("role") RoleEntity role, @Param("email") String email, @Param("phone") String phone);

}
