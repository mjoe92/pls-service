package de.vw.paso.service.partlist.efselementhistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record EfsElementAndMaraAndHistoryListDTO(
    Collection<EfsElementAndMaraAndHistoryDTO> efsElementAndMaraAndHistoryDTOList) {

    public List<EfsElementDTOWrapper> convertToEfsElementHistoryDTO() {
        List<EfsElementDTOWrapper> abstractEfsElementWrapperList = new ArrayList<>(
            efsElementAndMaraAndHistoryDTOList.size());

        for (EfsElementAndMaraAndHistoryDTO efsElementAndMaraAndHistoryDTO : efsElementAndMaraAndHistoryDTOList) {
            AbstractEfsElementDTO abstractEfsElement = efsElementAndMaraAndHistoryDTO.getEfsElementDTO() == null
                ? efsElementAndMaraAndHistoryDTO.getEfsElementHistoryDTO()
                : efsElementAndMaraAndHistoryDTO.getEfsElementDTO();

            AbstractEfsElementMaraDTO abstractEfsElementMara =
                efsElementAndMaraAndHistoryDTO.getEfsElementMaraDTO() == null
                    ? efsElementAndMaraAndHistoryDTO.getEfsElementMaraHistoryDTO()
                    : efsElementAndMaraAndHistoryDTO.getEfsElementMaraDTO();

            abstractEfsElementWrapperList.add(new EfsElementDTOWrapper(abstractEfsElement, abstractEfsElementMara));
        }

        return abstractEfsElementWrapperList;
    }
}
