package de.vw.paso.service.pls;

import de.vw.paso.pls.PartListStatus;
import de.vw.paso.service.vehicle.VehicleConfigDTO;

public record PlsRequestResultDTO(PartListStatus status, VehicleConfigDTO vehicleConfigDTO) { }
