package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.FileEntity;
import cz.meind.synchro.synchrobackend.database.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findFileEntitiesByUser(UserEntity user);

    @Transactional
    @Modifying
    @Query("DELETE FROM FileEntity f WHERE f.fileName = :fileName")
    void deleteDistinctByFileName(@Param("fileName") String fileName);}
