package de.vw.paso.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.model.Model;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.modelimport.ModelImportDTO;

public final class ModelImportMapper {

    public static ModelImport toEntity(ModelImportDTO entity) {
        ModelImport modelImport = new ModelImport();
        modelImport.setId(entity.getId());
        modelImport.setSalesKey(entity.getSalesKey());
        modelImport.setModelYear(entity.getModelYear());
        modelImport.setImportStatus(entity.getImportStatus());
        modelImport.setTimestampChange(entity.getTimestampChange());

        Set<Model> models = entity.getModels().stream().map(ModelMapper::toEntity).collect(Collectors.toSet());
        modelImport.setModels(models);

        SalesRegionDTO salesRegionDto = entity.getSalesRegion();
        if (salesRegionDto != null) {
            SalesRegion salesRegion = SalesRegionMapper.toEntity(salesRegionDto);
            modelImport.setSalesRegion(salesRegion);
        }

        return modelImport;
    }

    public static ModelImportDTO toDTO(ModelImport entity) {
        ModelImportDTO dto = new ModelImportDTO();
        dto.setId(entity.getId());
        dto.setSalesKey(entity.getSalesKey());
        dto.setModelYear(entity.getModelYear());
        dto.setImportStatus(entity.getImportStatus());
        dto.setSalesRegion(SalesRegionMapper.toDTO(entity.getSalesRegion()));
        dto.setTimestampChange(entity.getTimestampChange());

        Set<ModelDTO> models = entity.getModels().stream().map(ModelMapper::toDto).collect(Collectors.toSet());
        dto.setModels(models);

        return dto;
    }
}
