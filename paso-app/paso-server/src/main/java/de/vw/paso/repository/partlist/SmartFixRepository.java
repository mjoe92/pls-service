package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.smartfix.SmartFix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SmartFixRepository extends JpaRepository<SmartFix, Long> {

    List<SmartFix> findByActiveTrue();

    @Query("from SmartFix where field in (:fields)")
    Collection<SmartFix> findByField(@Param("fields") Collection<String> fields);
}
