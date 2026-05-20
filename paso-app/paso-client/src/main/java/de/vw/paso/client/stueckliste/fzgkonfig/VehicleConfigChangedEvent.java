package de.vw.paso.client.stueckliste.fzgkonfig;

import de.vw.paso.service.vehicle.VehicleConfigDTO;

public record VehicleConfigChangedEvent(VehicleConfigDTO vehicleConfig) { }
