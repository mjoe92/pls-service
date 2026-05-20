package de.vw.paso.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.model.Model;
import de.vw.paso.model.ModelImport;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.partlist.domain.VehiclePartList;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;
import de.vw.paso.service.masterdata.vehicleproject.VehicleProjectDTO;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.modelimport.ModelImportDTO;
import de.vw.paso.service.tiwhimport.TiWhImportDTO;
import de.vw.paso.service.user.ResourceDTO;
import de.vw.paso.service.user.VehiclePartListDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.tiwh.domain.TiWhImport;
import de.vw.paso.vehicle.domain.VehicleConfig;

public final class VehiclePartListMapper {

    //TODO Add check for single root (might need reference to EfsElement)
    public static VehiclePartList toEntityByDtoConfig(VehiclePartListDTO dto, VehicleConfigDTO vehicleConfigDto) {
        VehicleConfig vehicleConfig = vehicleConfigDto == null ? null : VehicleConfigMapper.toEntity(vehicleConfigDto);
        return toEntity(dto, vehicleConfig);
    }

    public static VehiclePartList toEntity(VehiclePartListDTO dto, VehicleConfig vehicleConfig) {
        VehiclePartList entity = new VehiclePartList();
        entity.setId(dto.getId());
        entity.setRevision(dto.getRevision());
        entity.setWeight(dto.getWeight());
        entity.setProductKeyVehicle(dto.getProductKeyVehicle());
        entity.setProductKeyMotor(dto.getProductKeyMotor());
        entity.setProductKeyGearbox(dto.getProductKeyGearbox());
        entity.setUserCreate(dto.getUserCreate());
        entity.setUserChange(dto.getUserChange());
        entity.setTimestampCreate(dto.getTimestampCreate());
        entity.setTimestampChange(dto.getTimestampChange());
        entity.setEntityChange(dto.isEntityChange());

        if (vehicleConfig != null) {
            entity.setVehicleConfig(vehicleConfig);
        }

        return entity;
    }

    public static VehiclePartListDTO toDTO(VehiclePartList entity, VehicleConfig vehicleConfig) {
        VehiclePartListDTO vehiclePartListDTO = new VehiclePartListDTO();
        vehiclePartListDTO.setId(entity.getId());
        vehiclePartListDTO.setRevision(entity.getRevision());
        vehiclePartListDTO.setWeight(entity.getWeight());
        vehiclePartListDTO.setProductKeyVehicle(entity.getProductKeyVehicle());
        vehiclePartListDTO.setProductKeyMotor(entity.getProductKeyMotor());
        vehiclePartListDTO.setProductKeyGearbox(entity.getProductKeyGearbox());
        vehiclePartListDTO.setUserCreate(entity.getUserCreate());
        vehiclePartListDTO.setUserChange(entity.getUserChange());
        vehiclePartListDTO.setTimestampCreate(entity.getTimestampCreate());
        vehiclePartListDTO.setTimestampChange(entity.getTimestampChange());
        vehiclePartListDTO.setEntityChange(entity.isEntityChange());

        if (vehicleConfig != null) {
            VehicleConfigDTO dto = createConfig(vehicleConfig);
            vehiclePartListDTO.setVehicleConfig(dto);
        }

        return vehiclePartListDTO;
    }

    private static VehicleConfigDTO createConfig(VehicleConfig entity) {
        VehicleConfigDTO dto = new VehicleConfigDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setValidDate(entity.getValidDate());
        dto.setModelYear(entity.getModelYear());
        dto.setSetVersionId(entity.getSetVersionId());
        dto.setCostGroupVersion(entity.getCostGroupVersion());
        dto.setDeletionDate(entity.getDeletionDate());
        dto.setPlsProductDataId(entity.getPlsProductDataId());
        dto.setPlsDataId(entity.getPlsDataId());
        dto.setPlsDataLockId(entity.getPlsDataLockId());
        dto.setStatus(entity.getStatus());
        dto.setRequestPosition(entity.getRequestPosition());
        dto.setSmartFixesActive(entity.isSmartFixesActive());
        dto.setUserCreate(entity.getUserCreate());
        dto.setUserChange(entity.getUserChange());
        dto.setTimestampCreate(entity.getTimestampCreate());
        dto.setTimestampChange(entity.getTimestampChange());
        dto.setEntityChange(entity.isEntityChange());
        dto.setEditAllowed(entity.isEditable());
        dto.setPrNumberString(entity.getPrNumberString());

        ResourceDTO resource = ResourceMapper.toDto(entity.getResource());
        dto.setResource(resource);

        VehicleProjectDTO vehicleProject = VehicleProjectMapper.toDto(entity.getVehicleProject());
        dto.setVehicleProject(vehicleProject);

        ModelImport modelImport = entity.getModelImport();
        ModelImportDTO modelImportDTO = modelImport == null ? null : ModelImportMapper.toDTO(modelImport);
        dto.setModelImport(modelImportDTO);

        Model model = entity.getModel();
        ModelDTO modelDTO = model == null ? null : ModelMapper.toDto(model);
        dto.setModel(modelDTO);

        SalesRegion salesRegion = entity.getSalesRegion();
        SalesRegionDTO salesRegionDTO = salesRegion == null ? null : SalesRegionMapper.toDTO(salesRegion);
        dto.setSalesRegion(salesRegionDTO);

        SetVersion setVersion = entity.getSetVersion();
        SetVersionDTO setVersionDTO = setVersion == null ? null : SetVersionMapper.toDto(setVersion);
        dto.setSetVersion(setVersionDTO);
        dto.setVehicleConfigCategoryStatus(
                entity.getVehicleConfigCategoryStatus().stream().map(VehicleConfigCategoryStatusMapper::toDto)
                        .toList());
        TiWhImport tiWhImportVehicle = entity.getTiWhImportVehicle();
        TiWhImportDTO tiWhImportVehicleDTO =
                tiWhImportVehicle == null ? null : TiWhImport.convertToTiWhImportDTO(tiWhImportVehicle);
        dto.setTiWhImportVehicle(tiWhImportVehicleDTO);

        TiWhImport tiWhImportMotor = entity.getTiWhImportMotor();
        TiWhImportDTO tiWhImportMotorDTO =
                tiWhImportMotor == null ? null : TiWhImport.convertToTiWhImportDTO(tiWhImportMotor);
        dto.setTiWhImportMotor(tiWhImportMotorDTO);

        TiWhImport tiWhImportGearbox = entity.getTiWhImportGearbox();
        TiWhImportDTO tiWhImportGearboxDTO =
                tiWhImportGearbox == null ? null : TiWhImport.convertToTiWhImportDTO(tiWhImportGearbox);
        dto.setTiWhImportGearbox(tiWhImportGearboxDTO);
        dto.setUserGroups(entity.getUserGroups().stream()
                .map(userGroup -> UserGroupMapper.toDto(userGroup, userGroup.getUsers(), null, null))
                .collect(Collectors.toCollection(ArrayList::new)));
        dto.setOwnerGroup(UserGroupMapper.toDto(entity.getOwnerGroup(), null, null, null));

        return dto;
    }
}
