package de.vw.paso.repository.masterdata;

import java.util.Collection;

import de.vw.paso.masterdata.domain.VehicleProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleProjectRepository extends JpaRepository<VehicleProject, Long> {

    @Modifying
    @Query("UPDATE VehicleProject AS v SET v.archive = :isArchived WHERE v.id IN :vehicleProjectIds")
    void updateArchiveStates(@Param("vehicleProjectIds") Collection<Long> vehicleProjectIds,
            @Param("isArchived") Boolean isArchived);
}
