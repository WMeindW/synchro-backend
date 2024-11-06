package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.CheckEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface CheckRepository extends JpaRepository<CheckEntity, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE CheckEntity c SET c.checkOut = :checkOut, c.checked = false WHERE c.user = :user and c.checked = true")
    void updateChecked(@Param("user") UserEntity user, @Param("checkOut") Timestamp checkOut);


}
