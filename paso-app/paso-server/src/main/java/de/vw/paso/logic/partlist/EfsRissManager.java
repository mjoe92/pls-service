package de.vw.paso.logic.partlist;

import java.util.Collection;

import de.vw.paso.mapper.EfsElementMapper;
import de.vw.paso.partlist.domain.FilteredOutEfsElement;
import de.vw.paso.repository.partlist.FilteredOutEfsElementRepository;
import de.vw.paso.service.partlist.efsriss.AlternativePartsForGapDTO;
import de.vw.paso.service.partlist.efsriss.AlternativePartsForGapListDTO;
import de.vw.paso.utility.EfsElementUtil;
import org.springframework.stereotype.Service;

@Service
public class EfsRissManager {

    private final FilteredOutEfsElementRepository filteredOutEfsElementRepository;

    public EfsRissManager(FilteredOutEfsElementRepository filteredOutEfsElementRepository) {
        this.filteredOutEfsElementRepository = filteredOutEfsElementRepository;
    }

    public AlternativePartsForGapListDTO getAlternativePartsForGap(String nodeId, long vehicleConfigId) {
        Collection<FilteredOutEfsElement> filteredOutEfsElements = filteredOutEfsElementRepository.findAllByVehicleConfigIdAndNodeId(
                vehicleConfigId, nodeId);

        Collection<AlternativePartsForGapDTO> alternativePartsForGapDTOList = filteredOutEfsElements.stream()
                .map(element -> new AlternativePartsForGapDTO(
                        EfsElementMapper.toDto(EfsElementUtil.filteredOutPartToEfsElement(element)),
                        element.getReason())).toList();

        return new AlternativePartsForGapListDTO(alternativePartsForGapDTOList);
    }
}
