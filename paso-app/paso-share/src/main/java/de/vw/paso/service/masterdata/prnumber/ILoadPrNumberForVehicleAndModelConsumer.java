package de.vw.paso.service.masterdata.prnumber;

import de.vw.paso.exception.ServiceConsumer;

public interface ILoadPrNumberForVehicleAndModelConsumer extends ServiceConsumer {

  public void loadPrNumbersForVehicleAndModel(Long vehicleProjectId, Long modelId);
}
