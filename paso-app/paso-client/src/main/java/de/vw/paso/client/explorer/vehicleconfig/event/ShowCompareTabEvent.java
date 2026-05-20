package de.vw.paso.client.explorer.vehicleconfig.event;

import java.util.List;

import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowCompareTabEvent {

    private final List<VehicleConfigDTO> vehicleConfigs;
    private final VehicleConfigDTO referenceVehicleConfig;

}
