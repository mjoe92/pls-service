package de.vw.paso.mapper;

import de.vw.paso.service.vehicle.VehicleConfigCategoryStatusDTO;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatusPK;

public final class VehicleConfigCategoryStatusMapper {

    public static VehicleConfigCategoryStatus toEntity(VehicleConfigCategoryStatusDTO dto) {
        VehicleConfigCategoryStatusPK pk = new VehicleConfigCategoryStatusPK();
        pk.setVehicleConfigId(dto.getVehicleConfigId());
        pk.setVehicleConfigCategory(dto.getVehicleConfigCategory());

        VehicleConfigCategoryStatus entity = new VehicleConfigCategoryStatus();
        entity.setVehicleConfigStatus(dto.getVehicleConfigStatus());
        entity.setId(pk);

        return entity;
    }

    public static VehicleConfigCategoryStatusDTO toDto(VehicleConfigCategoryStatus entity) {
        VehicleConfigCategoryStatusDTO vehicleConfigCategoryStatusDTO = new VehicleConfigCategoryStatusDTO();
        vehicleConfigCategoryStatusDTO.setVehicleConfigId(entity.getId().getVehicleConfigId());
        vehicleConfigCategoryStatusDTO.setVehicleConfigCategory(entity.getVehicleConfigCategory());
        vehicleConfigCategoryStatusDTO.setVehicleConfigStatus(entity.getVehicleConfigStatus());

        return vehicleConfigCategoryStatusDTO;
    }
}
