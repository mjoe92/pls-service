package de.vw.paso.repository.partlist;

import java.util.Optional;

import de.vw.paso.partlist.domain.SetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetVersionRepository extends JpaRepository<SetVersion, Long> {

    Optional<SetVersion> findByName(String name);
}
