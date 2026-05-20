package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.EfsElementMara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EfsElementMaraRepository extends JpaRepository<EfsElementMara, Long> {

    @Query("""
            FROM EfsElementMara
            WHERE id IN (:ids)
            """)
    List<EfsElementMara> findAllById(Collection<Long> ids);

    @Modifying
    @Transactional
    void deleteByVehiclePartListIdIn(Collection<Long> vehiclePartListIds);

    EfsElementMara findOneByPartNumberAndVehiclePartListId(String partNumber, Long vehiclePartListId);
}
