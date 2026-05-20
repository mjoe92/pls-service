package de.vw.paso.service.vehicle;

import de.vw.paso.exception.ServiceConsumer;

public interface ICreateVehiclePartListConsumer extends ServiceConsumer {

  void createVehiclePartList(long vehicleConfigId);

}
