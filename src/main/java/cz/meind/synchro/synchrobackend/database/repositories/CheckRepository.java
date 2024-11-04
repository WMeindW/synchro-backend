package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.CheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckRepository extends JpaRepository<CheckEntity, Long> {
}
