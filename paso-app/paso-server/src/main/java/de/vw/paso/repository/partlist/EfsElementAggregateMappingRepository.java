package de.vw.paso.repository.partlist;

import java.util.Collection;

import de.vw.paso.partlist.domain.EfsElementAggregateMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EfsElementAggregateMappingRepository extends JpaRepository<EfsElementAggregateMapping, Long> {

    @Query("""
            SELECT mapping FROM EfsElementAggregateMapping mapping
            WHERE mapping.efsElementId IN (
            SELECT efs.id FROM EfsElement efs WHERE efs.vehiclePartListId = :vehiclePartListId )
            """)
    Collection<EfsElementAggregateMapping> findByVehiclePartListId(Long vehiclePartListId);

    @Modifying
    @Transactional
    void deleteByEfsElementIdIn(Collection<Long> efsElementIds);
}
