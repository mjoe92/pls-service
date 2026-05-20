package de.vw.paso.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.masterdata.domain.VehicleProject;
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
import de.vw.paso.service.vehicle.OwnedVehicleConfigDTO;
import de.vw.paso.service.vehicle.VehicleConfigDTO;
import de.vw.paso.tiwh.domain.TiWhImport;
import de.vw.paso.user.domain.Resource;
import de.vw.paso.user.domain.UserGroup;
import de.vw.paso.vehicle.domain.VehicleConfig;
import de.vw.paso.vehicle.domain.VehicleConfigCategoryStatus;

public final class VehicleConfigMapper {

    public static VehicleConfig toEntity(VehicleConfigDTO dto) {
        VehicleConfig vehicleConfig = new VehicleConfig();
        vehicleConfig.setId(dto.getId());
        SalesRegionDTO salesRegionDto = dto.getSalesRegion();
        if (salesRegionDto != null) {
            SalesRegion salesRegion = SalesRegionMapper.toEntity(salesRegionDto);
            vehicleConfig.setSalesRegion(salesRegion);
        }

        vehicleConfig.setName(dto.getName());
        vehicleConfig.setDescription(dto.getDescription());
        vehicleConfig.setValidDate(dto.getValidDate());

        String prNumberString = dto.getPrNumberString();
        vehicleConfig.setPrNumberString(prNumberString);
        vehicleConfig.setModelYear(dto.getModelYear());
        vehicleConfig.setSetVersionId(dto.getSetVersionId());
        vehicleConfig.setCostGroupVersion(dto.getCostGroupVersion());
        vehicleConfig.setDeletionDate(dto.getDeletionDate());
        vehicleConfig.setPlsProductDataId(dto.getPlsProductDataId());
        vehicleConfig.setPlsDataLockId(dto.getPlsDataLockId());
        vehicleConfig.setStatus(dto.getStatus());
        vehicleConfig.setRequestPosition(dto.getRequestPosition());
        vehicleConfig.setSmartFixesActive(dto.isSmartFixesActive());
        vehicleConfig.setUserCreate(dto.getUserCreate());
        vehicleConfig.setUserChange(dto.getUserChange());
        vehicleConfig.setTimestampCreate(dto.getTimestampCreate());
        vehicleConfig.setTimestampChange(dto.getTimestampChange());
        vehicleConfig.setEntityChange(dto.isEntityChange());

        ResourceDTO resourceDto = dto.getResource();
        if (resourceDto != null) {
            Resource resource = ResourceMapper.toEntity(resourceDto);
            vehicleConfig.setResource(resource);
        }

        VehicleProject vehicleProject = VehicleProjectMapper.toEntity(dto.getVehicleProject());
        vehicleConfig.setVehicleProject(vehicleProject);

        ModelImportDTO modelImportDto = dto.getModelImport();
        if (modelImportDto != null) {
            ModelImport modelImport = ModelImportMapper.toEntity(modelImportDto);
            vehicleConfig.setModelImport(modelImport);
        }

        ModelDTO modelDto = dto.getModel();
        if (modelDto != null) {
            Model model = ModelMapper.toEntity(modelDto);
            vehicleConfig.setModel(model);
        }

        VehiclePartListDTO partListDto = dto.getVehiclePartList();
        if (partListDto != null) {
            VehiclePartList partList = VehiclePartListMapper.toEntity(partListDto, null);
            vehicleConfig.setVehiclePartList(partList);
        }

        SetVersion setVersion = SetVersionMapper.toEntity(dto.getSetVersion());
        vehicleConfig.setSetVersion(setVersion);

        List<VehicleConfigCategoryStatus> statuses = dto.getVehicleConfigCategoryStatus().stream()
                .map(VehicleConfigCategoryStatusMapper::toEntity).collect(Collectors.toList());
        vehicleConfig.setVehicleConfigCategoryStatus(statuses);

        TiWhImport tiWhImportVehicle = TiWhImport.convertToTiWhImportEntity(dto.getTiWhImportVehicle());
        vehicleConfig.setTiWhImportVehicle(tiWhImportVehicle);

        TiWhImport tiWhImportMotor = TiWhImport.convertToTiWhImportEntity(dto.getTiWhImportMotor());
        vehicleConfig.setTiWhImportMotor(tiWhImportMotor);

        TiWhImport tiWhImportGearbox = TiWhImport.convertToTiWhImportEntity(dto.getTiWhImportGearbox());
        vehicleConfig.setTiWhImportGearbox(tiWhImportGearbox);

        Set<UserGroup> userGroups = dto.getUserGroups().stream()
                .map(userGroup -> UserGroupMapper.toEntity(userGroup, userGroup.getUsers(),
                        userGroup.getVehicleConfigs(), userGroup.getOwnedVehicleConfigs())).collect(Collectors.toSet());
        vehicleConfig.setUserGroups(userGroups);

        UserGroup userGroup = UserGroupMapper.toEntity(dto.getOwnerGroup(), null, null, null);
        vehicleConfig.setOwnerGroup(userGroup);

        return vehicleConfig;
    }

    public static OwnedVehicleConfigDTO toDto(VehicleConfig vehicleConfig) {
        return new OwnedVehicleConfigDTO(vehicleConfig.getId(), vehicleConfig.getName());
    }

    //TODO: look into if we can determine the isEditable var's value in more places
    public static VehicleConfigDTO toDto(VehicleConfig entity, boolean isEditable) {
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
        dto.setEditAllowed(isEditable);
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

        VehiclePartList vehiclePartList = entity.getVehiclePartList();
        VehiclePartListDTO vehiclePartListDTO = vehiclePartList == null ? null :
                VehiclePartListMapper.toDTO(vehiclePartList, vehiclePartList.getVehicleConfig());
        dto.setVehiclePartList(vehiclePartListDTO);

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

    public static VehicleConfig toEntity(OwnedVehicleConfigDTO dto) {
        VehicleConfig entity = new VehicleConfig();
        entity.setId(dto.getId());
        entity.setName(dto.getOwnedVehicleConfigName());

        return entity;
    }
}
