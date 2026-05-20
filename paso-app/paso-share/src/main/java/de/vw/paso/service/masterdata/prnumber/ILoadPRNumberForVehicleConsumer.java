package de.vw.paso.service.masterdata.prnumber;

import de.vw.paso.exception.ServiceConsumer;

public interface ILoadPRNumberForVehicleConsumer extends ServiceConsumer {

  public void loadPrNumbersForVehicle(Long vehicleProjectId);
}
