package de.vw.paso.client.explorer.vehicleconfig.event;

import de.vw.paso.masterdata.Brand;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class FzgKonfigEvent {

  private final Brand brand;
  private final VehicleProjectDTO vehicleProject;

  public FzgKonfigEvent(final Brand brand) {
    this.brand = brand;
    this.vehicleProject = null;
  }

  public FzgKonfigEvent(final VehicleProjectDTO vehicleProject) {
    this.brand = null;
    this.vehicleProject = vehicleProject;
  }

}
