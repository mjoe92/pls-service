package de.vw.paso.client.stueckliste.compare;

import java.util.Collection;

import de.vw.paso.service.vehicle.VehicleConfigDTO;

public record ReopenCompareTabsEvent(Collection<VehicleConfigDTO> vehicleConfigs) { }
