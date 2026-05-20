package de.vw.paso.mapper;

import java.util.Collection;

import de.vw.paso.pr.PrNumberFamily;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;

public final class PrNumberFamilyMapper {

    public static PrNumberFamily toEntity(String prFamilyName, String descDe, String descEng) {
        PrNumberFamily entity = new PrNumberFamily();
        entity.setName(prFamilyName);
        entity.setDescriptionDe(descDe);
        entity.setDescriptionEn(descEng);

        return entity;
    }

    public static PrNumberFamilyDTO toDto(Long id, String name, String description, Collection<PrNumberDTO> prNumbers) {
        return new PrNumberFamilyDTO(id, name, description, prNumbers);
    }
}
