package de.vw.paso.client.explorer.vehicleconfig.event;

import de.vw.paso.service.vehicle.VehicleConfigDTO;

public record ShowEfsTabEvent(VehicleConfigDTO vehicleConfig, boolean partListCreated) { }
