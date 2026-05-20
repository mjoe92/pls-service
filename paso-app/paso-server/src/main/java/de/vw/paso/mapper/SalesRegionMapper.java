package de.vw.paso.mapper;

import de.vw.paso.masterdata.domain.SalesRegion;
import de.vw.paso.service.masterdata.salesregion.SalesRegionDTO;

public final class SalesRegionMapper {

    public static SalesRegion toEntity(SalesRegionDTO dto) {
        SalesRegion entity = new SalesRegion();
        entity.setId(dto.id());
        entity.setRelevant(dto.relevant());
        entity.setDescriptionEn(dto.descriptionEn());
        entity.setDescriptionDe(dto.descriptionDe());

        return entity;
    }

    public static SalesRegionDTO toDTO(SalesRegion entity) {
        return new SalesRegionDTO(entity.getId(), entity.getRelevant(), entity.getDescriptionDe(),
                entity.getDescriptionEn());
    }
}
