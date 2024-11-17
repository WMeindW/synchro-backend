package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.MotdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MotdRepository extends JpaRepository<MotdEntity, Long> {

    @Query("SELECT e FROM MotdEntity e WHERE e.id = (SELECT MAX(id) FROM MotdEntity )")
    MotdEntity findMaxIdEntity();

}
