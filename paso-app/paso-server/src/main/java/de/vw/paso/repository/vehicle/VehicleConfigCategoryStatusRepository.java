package de.vw.paso.repository.vehicle;

import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatusPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleConfigCategoryStatusRepository
  extends JpaRepository<VehicleConfigCategoryStatus, VehicleConfigCategoryStatusPK> {

  VehicleConfigCategoryStatus findOneByVehicleConfigAndIdVehicleConfigCategory(VehicleConfig vehicleConfig,
    VehicleConfigCategory vehicleConfigCategory);

}
