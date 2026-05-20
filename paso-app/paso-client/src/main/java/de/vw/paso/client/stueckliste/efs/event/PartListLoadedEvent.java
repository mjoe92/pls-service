package de.vw.paso.client.stueckliste.efs.event;

import de.vw.paso.service.vehicle.VehicleConfigDTO;

public class PartListLoadedEvent {

  //TODO Change to part list id
  private VehicleConfigDTO vehicleConfig;

  public PartListLoadedEvent(VehicleConfigDTO vehicleConfig) {
    this.vehicleConfig = vehicleConfig;
  }

  public VehicleConfigDTO getVehicleConfig() {
    return vehicleConfig;
  }
}
