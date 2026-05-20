package de.vw.paso.repository.partlist;

import java.util.Optional;

import de.vw.paso.partlist.domain.Pst;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PstRepository extends JpaRepository<Pst, Long> {

    Optional<Pst> findByName(String name);
}
