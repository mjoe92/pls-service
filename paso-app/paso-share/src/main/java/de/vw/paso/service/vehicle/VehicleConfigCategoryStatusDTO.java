package de.vw.paso.service.vehicle;

import de.vw.paso.core.domain.AbstractDTO;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.VehicleConfigStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class VehicleConfigCategoryStatusDTO extends AbstractDTO<Long> {
  private Long vehicleConfigId;
  private VehicleConfigCategory vehicleConfigCategory;
  private VehicleConfigStatus vehicleConfigStatus;

  public void setVehicleConfigStatus(VehicleConfigStatus vehicleConfigStatus) {
    this.vehicleConfigStatus = vehicleConfigStatus;
  }

  @Override
  public Long getId() {
    return vehicleConfigId;
  }

  @Override
  public void setId(Long vehicleConfigId) {
    this.vehicleConfigId = vehicleConfigId;
  }
}
