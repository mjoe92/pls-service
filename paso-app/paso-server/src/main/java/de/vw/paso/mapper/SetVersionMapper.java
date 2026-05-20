package de.vw.paso.mapper;

import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.service.masterdata.setversion.SetVersionDTO;

public final class SetVersionMapper {

    public static SetVersion toEntity(SetVersionDTO dto) {
        if (dto == null) {
            return null;
        }

        SetVersion entity = new SetVersion();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setUserCreate(dto.getUserCreate());
        entity.setUserChange(dto.getUserChange());
        entity.setTimestampCreate(dto.getTimestampCreate());
        entity.setTimestampChange(dto.getTimestampChange());

        return entity;
    }

    public static SetVersionDTO toDto(SetVersion entity) {
        if (entity == null) {
            return null;
        }

        SetVersionDTO dto = new SetVersionDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUserCreate(entity.getUserCreate());
        dto.setUserChange(entity.getUserChange());
        dto.setTimestampCreate(entity.getTimestampCreate());
        dto.setTimestampChange(entity.getTimestampChange());

        return dto;
    }
}
