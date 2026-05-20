package de.vw.paso.service.user;

import java.util.Map;

public record VehicleConfigsAccessDTO(Map<Long, Boolean> vehiclesWithAccess) {
}
