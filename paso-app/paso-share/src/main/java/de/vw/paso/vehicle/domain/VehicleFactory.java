package de.vw.paso.vehicle.domain;

import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.vehicle.VehicleConfigCategory;
import de.vw.paso.vehicle.VehicleConfigStatus;

public final class VehicleFactory {

    public static VehicleConfigDTO createFzgConfig(final String bezeichnung, final VehicleProjectDTO vehicleProject) {
        VehicleConfigDTO vehicleConfigDTO = new VehicleConfigDTO();
        vehicleConfigDTO.setName(bezeichnung);
        vehicleConfigDTO.setVehicleProject(vehicleProject);
        return vehicleConfigDTO;
    }

    public static VehicleConfigCategoryStatus createFogConfigCategoryStatus(
            VehicleConfigCategory vehicleConfigCategory) {
        VehicleConfigCategoryStatusPK pk = new VehicleConfigCategoryStatusPK();
        pk.setVehicleConfigCategory(vehicleConfigCategory);

        VehicleConfigCategoryStatus vehicleConfigCategoryStatus = new VehicleConfigCategoryStatus();
        vehicleConfigCategoryStatus.setId(pk);
        vehicleConfigCategoryStatus.setVehicleConfigStatus(VehicleConfigStatus.INITIAL);

        return vehicleConfigCategoryStatus;
    }

}
