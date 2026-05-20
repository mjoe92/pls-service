package de.vw.paso.client.explorer.vehicleconfig.event;

import java.util.Collections;
import java.util.List;

import de.vw.paso.service.vehicle.VehicleConfigDTO;
import lombok.Getter;

public class UpdateVehicleConfigEvent {

    public enum UpdateEventType {
        CREATE, UPDATE, DELETE
    }

    @Getter
    private final List<VehicleConfigDTO> vehicleConfig;
    private final UpdateEventType actionType;

    public UpdateVehicleConfigEvent(VehicleConfigDTO vehicleConfig, UpdateEventType actionType) {
        this(Collections.singletonList(vehicleConfig), actionType);
    }

    public UpdateVehicleConfigEvent(List<VehicleConfigDTO> vehicleConfigs, UpdateEventType actionType) {
        this.vehicleConfig = vehicleConfigs;
        this.actionType = actionType;
    }

    public UpdateEventType getUpdateEventType() {
        return actionType;
    }
}
