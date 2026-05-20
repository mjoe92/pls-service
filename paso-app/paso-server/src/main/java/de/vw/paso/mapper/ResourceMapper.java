package de.vw.paso.mapper;

import de.vw.paso.service.user.ResourceDTO;
import de.vw.paso.user.domain.Resource;

public final class ResourceMapper {

    public static Resource toEntity(ResourceDTO resourceDTO) {
        Resource resource = new Resource();
        resource.setId(resourceDTO.getId());
        resource.setType(resourceDTO.getType());

        return resource;
    }

    public static ResourceDTO toDto(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setType(resource.getType());

        return dto;
    }
}
