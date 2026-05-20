package de.vw.paso.service.partlist.efsriss;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import de.vw.paso.service.partlist.efsedit.EfsElementDTO;

public record AlternativePartsForGapListDTO(Collection<AlternativePartsForGapDTO> alternativePartsForGapDTOList) {

    public static Map<EfsElementDTO, String> createEfsElementMap(
            AlternativePartsForGapListDTO alternativePartsForGapListDTO) {
        return alternativePartsForGapListDTO.alternativePartsForGapDTOList().stream().collect(
                Collectors.toMap(AlternativePartsForGapDTO::efsElementDTO, AlternativePartsForGapDTO::value,
                        (first, second) -> second));
    }
}
