package de.vw.paso.repository.partlist;

import de.vw.paso.partlist.domain.VehiclePartList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface VehiclePartListRepository extends JpaRepository<VehiclePartList, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE VehiclePartList AS v SET v.weight = :weight WHERE v.id = :vehiclePartListId")
  void updateWeight(@Param("vehiclePartListId") Long vehiclePartListId,
                    @Param("weight") Double weight);
}
