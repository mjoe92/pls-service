package de.vw.paso.service.vehicle;

import de.vw.paso.exception.ServiceConsumer;

public interface ISaveVehicleConfigConsumer extends ServiceConsumer {

  void saveVehicleConfig(VehicleConfigDTO vehicleConfig, Runnable onSaveSuccess);

}
