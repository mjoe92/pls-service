package de.vw.paso.compare;

import java.util.List;

import de.vw.paso.service.vehicle.VehicleConfigDTO;

public abstract class AbstractCompareResult {

    public abstract List<VehicleConfigDTO> getVehicleConfigs();

    public abstract VehicleConfigDTO getReference();
}
