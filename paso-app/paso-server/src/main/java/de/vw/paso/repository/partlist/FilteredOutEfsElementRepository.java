package de.vw.paso.repository.partlist;

import java.util.Collection;
import java.util.List;

import de.vw.paso.partlist.domain.FilteredOutEfsElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface FilteredOutEfsElementRepository extends JpaRepository<FilteredOutEfsElement, Long> {

    List<FilteredOutEfsElement> findAllByVehicleConfigIdAndNodeId(Long vehicleConfigId, String nodeId);

    @Modifying
    @Transactional
    void deleteByVehicleConfigIdIn(Collection<Long> ids);
}
