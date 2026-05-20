package de.vw.paso.service.masterdata.vehicleproject;

import java.util.List;

public record UpdateVehicleProjectArchiveStateDTO(List<Long> vehicleProjectIds, boolean isArchived) {
}
