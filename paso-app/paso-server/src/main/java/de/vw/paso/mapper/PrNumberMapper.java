package de.vw.paso.mapper;

import java.util.Date;

import de.vw.paso.pr.PrNumber;
import de.vw.paso.pr.PrNumberFamily;
import de.vw.paso.service.masterdata.prnumber.PrNumberDTO;
import de.vw.paso.service.masterdata.prnumber.PrNumberFamilyDTO;

public final class PrNumberMapper {

    public static PrNumber toEntity(String name, String descriptionDe, String descriptionEng, PrNumberFamily family) {
        PrNumber prNumber = new PrNumber();
        prNumber.setName(name);
        prNumber.setDescriptionDe(descriptionDe);
        prNumber.setDescriptionEn(descriptionEng);
        prNumber.setPrNumberFamily(family);

        return prNumber;
    }

    public static PrNumberDTO toDTO(Long id, Long assignmentId, String name, String description, Integer status,
            Date startDate, String startKey, Date endDate, String endKey, String additionalName,
            PrNumberFamilyDTO prNumberFamilyDTO) {
        return new PrNumberDTO(id, assignmentId, name, description, status, startDate, startKey, endDate, endKey,
                additionalName, prNumberFamilyDTO);
    }

    // todo: change this hacky way to correct mapping where it has been called -> backreference the PrFamily?
    public static PrNumberDTO toDTO(PrNumber prNumber, Long assignmentId, boolean isGerman) {
        String description = isGerman ? prNumber.getDescriptionDe() : prNumber.getDescriptionEn();

        return new PrNumberDTO(prNumber.getId(), assignmentId, prNumber.getName(), description, null, null, null, null,
                null, null, null);
    }
}
