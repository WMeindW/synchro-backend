package cz.meind.synchro.synchrobackend.database.repositories;

import cz.meind.synchro.synchrobackend.database.entities.BlacklistJwtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistJwtRepository extends JpaRepository<BlacklistJwtEntity, Long> {

    Optional<BlacklistJwtEntity> findBlacklistJwtEntityByJwtToken(String token);
}
