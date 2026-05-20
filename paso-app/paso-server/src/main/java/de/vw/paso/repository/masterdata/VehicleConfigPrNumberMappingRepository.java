package de.vw.paso.repository.masterdata;

import java.util.Collection;

import de.vw.paso.pr.VehicleConfigPrNumberMapping;
import de.vw.paso.pr.VehicleConfigPrNumberMappingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehicleConfigPrNumberMappingRepository
        extends JpaRepository<VehicleConfigPrNumberMapping, VehicleConfigPrNumberMappingId> {

    @Query("""
                SELECT mapping
                FROM VehicleConfigPrNumberMapping mapping
                WHERE mapping.id.vehicleConfigId = :vehicleConfigId
            """)
    Collection<VehicleConfigPrNumberMapping> findAllPrNumberIdsByConfigId(Long vehicleConfigId);

    void deleteById_VehicleConfigIdIn(Collection<Long> vehicleConfigIds);
}
