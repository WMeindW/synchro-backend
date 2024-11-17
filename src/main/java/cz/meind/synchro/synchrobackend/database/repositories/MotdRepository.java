package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.MotdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotdRepository extends JpaRepository<MotdEntity, Long> {

}
