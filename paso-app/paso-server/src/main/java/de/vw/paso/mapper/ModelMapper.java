package de.vw.paso.mapper;

import de.vw.paso.model.Model;
import de.vw.paso.model.ModelImport;
import de.vw.paso.service.modelimport.ModelDTO;
import de.vw.paso.service.modelimport.ModelImportDTO;

public final class ModelMapper {

    public static Model toEntity(ModelDTO dto) {
        Model model = new Model();
        model.setId(dto.getId());
        model.setModelKey(dto.getModelKey());
        model.setDescription(dto.getDescription());
        model.setStatus(dto.getStatus());
        model.setModelVersion(dto.getModelVersion());
        model.setBeginDate(dto.getBeginDate());
        model.setEndDate(dto.getEndDate());

        ModelImportDTO modelImportDto = dto.getModelImport();
        if (modelImportDto != null) {
            ModelImport modelImport = ModelImportMapper.toEntity(dto.getModelImport());
            model.setModelImport(modelImport);
        }

        return model;
    }

    public static ModelDTO toDto(Model entity) {
        ModelDTO modelDTO = new ModelDTO();
        modelDTO.setId(entity.getId());
        modelDTO.setModelKey(entity.getModelKey());
        modelDTO.setDescription(entity.getDescription());
        modelDTO.setStatus(entity.getStatus());
        modelDTO.setModelVersion(entity.getModelVersion());
        modelDTO.setBeginDate(entity.getBeginDate());
        modelDTO.setEndDate(entity.getEndDate());

        return modelDTO;
    }
}
