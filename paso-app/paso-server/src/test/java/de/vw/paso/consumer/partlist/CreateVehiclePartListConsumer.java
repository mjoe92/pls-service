package de.vw.paso.consumer.partlist;

import de.vw.paso.core.AbstractTestConsumer;
import de.vw.paso.service.vehicle.ICreateVehiclePartListConsumer;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateVehiclePartListConsumer extends AbstractTestConsumer<VehicleConfigDTO> implements ICreateVehiclePartListConsumer {

  @Autowired
  private VehicleConfigRestService vehicleConfigService;


  @Override
  public void createVehiclePartList(final long vehicleConfigId) {
    run(()-> (vehicleConfigService.createVehiclePartList(vehicleConfigId)));
  }
}
