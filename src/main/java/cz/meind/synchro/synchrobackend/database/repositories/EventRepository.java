package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.EventEntity;
import cz.meind.synchro.synchrobackend.database.entities.EventTypeEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE EventEntity e SET e.deleted = :deleted WHERE e.id = :id")
    void updateEventEntityDeletedById(@Param("deleted") Boolean deleted, @Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE EventEntity e SET e.timeEnd = :timeEnd, e.timeStart = :timeStart, e.type = :type, e.user = :user WHERE e.id = :id")
    void updateEventEntityById(@Param("user") UserEntity user, @Param("timeEnd") Timestamp timeEnd, @Param("timeStart") Timestamp timeStart, @Param("type") EventTypeEntity type, @Param("id") Long id);

    List<EventEntity> findAllByTypeAndUser(EventTypeEntity type, UserEntity user);

    List<EventEntity> findAllByUser(UserEntity user);
}
